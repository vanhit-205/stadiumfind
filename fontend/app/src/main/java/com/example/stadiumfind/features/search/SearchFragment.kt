package com.example.stadiumfind.features.search

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.R
import com.example.stadiumfind.core.util.LocationHelper
import com.example.stadiumfind.core.util.OsmTileSources
import com.example.stadiumfind.databinding.FragmentSearchBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.presentation.bottomsheet.StadiumPreviewBottomSheet
import com.example.stadiumfind.features.stadium.presentation.detail.StadiumDetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import javax.inject.Inject

/**
 * Fragment hiển thị OpenStreetMap (osmdroid) toàn màn hình với:
 * - Location overlay hiển thị vị trí hiện tại
 * - Floating search bar
 * - FAB "Vị trí của tôi"
 * - Dark map filter phù hợp dark theme của ứng dụng
 * - Rải Marker các cụm sân bóng lân cận từ API backend
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private var locationOverlay: MyLocationNewOverlay? = null
    private val stadiumMarkers = mutableListOf<Marker>()

    @Inject
    lateinit var locationHelper: LocationHelper

    companion object {
        private const val TAG = "SearchFragment"
    }

    // Launcher xin quyền Location
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            Log.d(TAG, "Location permission granted")
            binding.layoutPermissionDenied.visibility = View.GONE
            onLocationPermissionGranted()
        } else {
            Log.w(TAG, "Location permission denied")
            onLocationPermissionDenied()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
        observeViewModel()

        // 1. Tải ngay danh sách sân bóng ban đầu (dùng tọa độ trung tâm) để luôn có Marker xuất hiện ngay lập tức
        viewModel.loadNearbyStadiums(
            latitude = LocationHelper.DEFAULT_LOCATION.latitude,
            longitude = LocationHelper.DEFAULT_LOCATION.longitude
        )

        // 2. Kiểm tra quyền vị trí và animate nếu có
        if (locationHelper.hasLocationPermission()) {
            onLocationPermissionGranted()
        } else {
            moveToDefaultLocation()
            requestLocationPermission()
        }
    }

    private fun setupMap() {
        binding.mapView.apply {
            // Nguồn OpenStreetMap độc lập (miễn phí 100%, không cần API key, không bị 403)
            setTileSource(OsmTileSources.OSM_PUBLIC)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // Áp dụng bộ lọc Dark Theme đồng bộ giao diện ứng dụng
            applyDarkMapStyle()

            // Thiết lập vị trí mặc định ban đầu
            controller.setZoom(LocationHelper.DEFAULT_ZOOM)
            controller.setCenter(LocationHelper.DEFAULT_LOCATION)
        }
    }

    private fun setupListeners() {
        // FAB: Quay về vị trí hiện tại và tải lại sân lân cận
        binding.fabMyLocation.setOnClickListener {
            if (locationHelper.hasLocationPermission()) {
                animateToCurrentLocation(forceRefreshNearby = true)
            } else {
                requestLocationPermission()
            }
        }

        // Nút cấp quyền trong banner
        binding.btnGrantPermission.setOnClickListener {
            requestLocationPermission()
        }

        // Tìm kiếm khi nhấn phím Enter trên Search Bar
        binding.etSearchMap.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchStadiumOnMap(query)
                }
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SearchUiState.Idle -> {
                            binding.progressLoadingMap.visibility = View.GONE
                        }
                        is SearchUiState.Loading -> {
                            binding.progressLoadingMap.visibility = View.VISIBLE
                        }
                        is SearchUiState.Success -> {
                            binding.progressLoadingMap.visibility = View.GONE
                            Log.d(TAG, "Đã nhận được ${state.stadiums.size} sân bóng từ API")
                            renderStadiumMarkers(state.stadiums)
                        }
                        is SearchUiState.Error -> {
                            binding.progressLoadingMap.visibility = View.GONE
                            Log.e(TAG, "Lỗi tải sân bóng: ${state.message}")
                            Toast.makeText(requireContext(), "Lỗi tải sân: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Chuyển Vector Drawable thành BitmapDrawable để osmdroid vẽ Marker chuẩn xác và không bị lỗi bounds.
     */
    private fun createMarkerIcon(): BitmapDrawable {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_map_stadium_pin)
        val density = resources.displayMetrics.density
        val width = (36 * density).toInt().coerceAtLeast(48)
        val height = (44 * density).toInt().coerceAtLeast(58)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.setBounds(0, 0, width, height)
        drawable?.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }

    /**
     * Rải các Marker sân bóng trực tiếp lên bản đồ osmdroid.
     */
    private fun renderStadiumMarkers(stadiums: List<StadiumItem>) {
        val mapView = binding.mapView

        // 1. Xóa các marker sân bóng cũ khỏi overlays
        stadiumMarkers.forEach { mapView.overlays.remove(it) }
        stadiumMarkers.clear()

        // 2. Tạo icon BitmapDrawable chuẩn
        val pinBitmapDrawable = createMarkerIcon()

        // 3. Tạo Marker cho từng sân
        var validCount = 0
        stadiums.forEach { stadium ->
            if (stadium.latitude != 0.0 && stadium.longitude != 0.0) {
                val marker = Marker(mapView).apply {
                    id = "stadium_${stadium.id}"
                    position = GeoPoint(stadium.latitude, stadium.longitude)
                    icon = pinBitmapDrawable
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = stadium.name
                    relatedObject = stadium
                    snippet = buildString {
                        if (stadium.formattedDistance.isNotBlank()) {
                            append("📍 ${stadium.formattedDistance} • ")
                        }
                        append(stadium.formattedPrice)
                    }
                    setOnMarkerClickListener { _, _ ->
                        showStadiumBottomSheet(stadium)
                        true
                    }
                }
                mapView.overlays.add(marker)
                stadiumMarkers.add(marker)
                validCount++
            }
        }

        Log.d(TAG, "Đã thêm $validCount markers vào bản đồ. Tổng overlays: ${mapView.overlays.size}")
        mapView.invalidate()
    }

    /**
     * Hiển thị BottomSheet xem nhanh thông tin sân khi click Marker và căn chỉnh camera offset mượt mà.
     */
    private fun showStadiumBottomSheet(stadium: StadiumItem) {
        // Tối ưu UX Camera: Dịch tâm bản đồ xuống phía Nam một chút để Marker nằm ở nửa trên màn hình,
        // không bị che khuất bởi chiều cao của BottomSheet khi bật lên
        val offsetLatitude = stadium.latitude - 0.0035
        val cameraFocusPoint = GeoPoint(offsetLatitude, stadium.longitude)
        binding.mapView.controller.animateTo(cameraFocusPoint, 15.5, 600L)

        // Đóng BottomSheet cũ nếu còn hiển thị
        val existingDialog = childFragmentManager.findFragmentByTag(StadiumPreviewBottomSheet.TAG) as? StadiumPreviewBottomSheet
        existingDialog?.dismiss()

        // Mở BottomSheet mới kèm callback điều hướng sang chi tiết sân
        val bottomSheet = StadiumPreviewBottomSheet.newInstance(stadium) { stadiumId ->
            val bundle = bundleOf(StadiumDetailFragment.ARG_STADIUM_ID to stadiumId)
            findNavController().navigate(R.id.action_searchFragment_to_stadiumDetailFragment, bundle)
        }
        bottomSheet.show(childFragmentManager, StadiumPreviewBottomSheet.TAG)
    }

    /**
     * Tìm nhanh vị trí sân theo tên trong danh sách Marker hiện tại.
     */
    private fun searchStadiumOnMap(query: String) {
        for (item in stadiumMarkers) {
            if (item.title?.contains(query, ignoreCase = true) == true) {
                val stadium = item.relatedObject as? StadiumItem
                if (stadium != null) {
                    showStadiumBottomSheet(stadium)
                } else {
                    binding.mapView.controller.animateTo(item.position, 16.0, 800L)
                    item.showInfoWindow()
                }
                return
            }
        }
        Toast.makeText(requireContext(), "Không tìm thấy sân: $query", Toast.LENGTH_SHORT).show()
    }

    /**
     * Áp dụng dark map style bằng ColorMatrix invert + dimmer cho tiles overlay.
     */
    private fun applyDarkMapStyle() {
        try {
            val inverseMatrix = ColorMatrix(
                floatArrayOf(
                    -0.85f, 0f, 0f, 0f, 220f,
                    0f, -0.85f, 0f, 0f, 220f,
                    0f, 0f, -0.85f, 0f, 220f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            binding.mapView.overlayManager.tilesOverlay.setColorFilter(
                ColorMatrixColorFilter(inverseMatrix)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error applying dark map style", e)
        }
    }

    /**
     * Xin quyền Location Runtime.
     */
    private fun requestLocationPermission() {
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (shouldShowRationale) {
            binding.layoutPermissionDenied.visibility = View.VISIBLE
        }

        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Khi được cấp quyền: bật overlay vị trí hiện tại & animate tới vị trí user.
     */
    @SuppressLint("MissingPermission")
    private fun onLocationPermissionGranted() {
        if (locationOverlay == null) {
            val provider = GpsMyLocationProvider(requireContext())
            locationOverlay = MyLocationNewOverlay(provider, binding.mapView).apply {
                enableMyLocation()
            }
            binding.mapView.overlays.add(locationOverlay)
        } else {
            locationOverlay?.enableMyLocation()
        }
        binding.mapView.invalidate()
        animateToCurrentLocation(forceRefreshNearby = false)
    }

    /**
     * Khi bị từ chối quyền: hiện banner hướng dẫn, giữ map ở vị trí mặc định.
     */
    private fun onLocationPermissionDenied() {
        binding.layoutPermissionDenied.visibility = View.VISIBLE
        moveToDefaultLocation()
    }

    /**
     * Di chuyển mượt (animate) tới vị trí hiện tại của user và tải các sân lân cận.
     */
    private fun animateToCurrentLocation(forceRefreshNearby: Boolean = false) {
        if (!locationHelper.isLocationServiceEnabled()) {
            Toast.makeText(requireContext(), "Vui lòng bật định vị GPS trên thiết bị để xác định vị trí chính xác", Toast.LENGTH_LONG).show()
        }

        binding.progressLoadingMap.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val currentLocation = locationHelper.getCurrentLocation()
            binding.progressLoadingMap.visibility = View.GONE

            val targetGeoPoint = if (currentLocation != null) {
                currentLocation
            } else {
                Toast.makeText(requireContext(), "Không thể lấy vị trí hiện tại, hiển thị vị trí mặc định", Toast.LENGTH_SHORT).show()
                LocationHelper.DEFAULT_LOCATION
            }

            binding.mapView.controller.animateTo(
                targetGeoPoint,
                LocationHelper.DEFAULT_ZOOM,
                800L
            )

            // Cập nhật danh sách sân bóng lân cận theo vị trí thực tế
            viewModel.loadNearbyStadiums(
                latitude = targetGeoPoint.latitude,
                longitude = targetGeoPoint.longitude,
                forceRefresh = forceRefreshNearby
            )
        }
    }

    /**
     * Di chuyển camera đến vị trí mặc định (Hà Nội).
     */
    private fun moveToDefaultLocation() {
        binding.mapView.controller.setCenter(LocationHelper.DEFAULT_LOCATION)
        binding.mapView.controller.setZoom(LocationHelper.DEFAULT_ZOOM)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        locationOverlay?.disableMyLocation()
        stadiumMarkers.forEach { binding.mapView.overlays.remove(it) }
        stadiumMarkers.clear()
        binding.mapView.onDetach()
        super.onDestroyView()
        _binding = null
    }
}
