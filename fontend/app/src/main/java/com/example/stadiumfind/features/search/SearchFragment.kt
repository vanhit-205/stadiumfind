package com.example.stadiumfind.features.search

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.stadiumfind.core.util.LocationHelper
import com.example.stadiumfind.core.util.OsmTileSources
import com.example.stadiumfind.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import javax.inject.Inject

/**
 * Fragment hiển thị OpenStreetMap (osmdroid) toàn màn hình với:
 * - Location overlay hiển thị vị trí hiện tại
 * - Floating search bar
 * - FAB "Vị trí của tôi"
 * - Dark map filter phù hợp dark theme của ứng dụng
 *
 * Ngày 16 sẽ bổ sung: rải Marker sân bóng, BottomSheet thông tin sân.
 */
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var locationOverlay: MyLocationNewOverlay? = null

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

        // Kiểm tra quyền và điều hướng vị trí
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
        // FAB: Quay về vị trí hiện tại
        binding.fabMyLocation.setOnClickListener {
            if (locationHelper.hasLocationPermission()) {
                animateToCurrentLocation()
            } else {
                requestLocationPermission()
            }
        }

        // Nút cấp quyền trong banner
        binding.btnGrantPermission.setOnClickListener {
            requestLocationPermission()
        }
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
        animateToCurrentLocation()
    }

    /**
     * Khi bị từ chối quyền: hiện banner hướng dẫn, giữ map ở vị trí mặc định.
     */
    private fun onLocationPermissionDenied() {
        binding.layoutPermissionDenied.visibility = View.VISIBLE
        moveToDefaultLocation()
    }

    /**
     * Di chuyển mượt (animate) tới vị trí hiện tại của user.
     */
    private fun animateToCurrentLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentLocation = locationHelper.getCurrentLocation()
            val targetGeoPoint = currentLocation ?: LocationHelper.DEFAULT_LOCATION

            binding.mapView.controller.animateTo(
                targetGeoPoint,
                LocationHelper.DEFAULT_ZOOM,
                800L
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
        binding.mapView.onDetach()
        super.onDestroyView()
        _binding = null
    }
}
