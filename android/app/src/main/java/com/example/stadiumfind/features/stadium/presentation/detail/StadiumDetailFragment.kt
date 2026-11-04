package com.example.stadiumfind.features.stadium.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.stadiumfind.R
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.databinding.FragmentStadiumDetailBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Màn hình Chi tiết Sân bóng (StadiumDetailFragment).
 * Nhận `stadiumId` từ Navigation arguments và hiển thị toàn bộ thông tin chi tiết:
 * - Image Slider (ViewPager2) kèm Dots indicator
 * - Thông tin sân: Tên, môn thể thao, đánh giá sao, số sân, địa chỉ, giờ hoạt động
 * - Nút gọi hotline trực tiếp và nút chỉ đường mở Google Maps
 * - Danh sách tiện ích sân (Wifi, Bãi xe, Nước uống, Phòng tắm, Đèn đêm, Cỏ nhân tạo)
 * - Mô tả chi tiết sân
 * - Thanh đặt sân cố định phía dưới (Bottom Action Bar)
 */
@AndroidEntryPoint
class StadiumDetailFragment : Fragment() {

    private var _binding: FragmentStadiumDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StadiumDetailViewModel by viewModels()

    private var currentStadium: StadiumItem? = null
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    companion object {
        const val ARG_STADIUM_ID = "stadiumId"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStadiumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()
        observeDetailState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        // Nút Thử lại khi gặp lỗi
        binding.btnRetry.setOnClickListener {
            viewModel.loadStadiumDetail()
        }

        // Nút Gọi Hotline
        binding.btnCallHotline.setOnClickListener {
            val phone = currentStadium?.phoneNumber
            if (!phone.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${phone.trim()}")
                }
                runCatching { startActivity(intent) }.onFailure {
                    Toast.makeText(requireContext(), "Không thể mở ứng dụng gọi điện", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Sân chưa cập nhật số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Chỉ đường
        binding.btnDirections.setOnClickListener {
            val stadium = currentStadium ?: return@setOnClickListener
            val lat = stadium.latitude
            val lng = stadium.longitude
            val intent = if (lat != 0.0 && lng != 0.0) {
                val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(stadium.name)})")
                Intent(Intent.ACTION_VIEW, uri)
            } else if (!stadium.address.isBlank()) {
                val uri = Uri.parse("geo:0,0?q=${Uri.encode(stadium.address)}")
                Intent(Intent.ACTION_VIEW, uri)
            } else {
                null
            }

            if (intent != null) {
                runCatching { startActivity(intent) }.onFailure {
                    Toast.makeText(requireContext(), "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Chưa có thông tin tọa độ hay địa chỉ sân", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút Đặt sân ngay
        binding.btnBookNowDetail.setOnClickListener {
            val stadium = currentStadium
            if (stadium != null) {
                val bundle = Bundle().apply {
                    putLong(com.example.stadiumfind.features.booking.BookingFragment.ARG_STADIUM_ID, stadium.id)
                    putString(com.example.stadiumfind.features.booking.BookingFragment.ARG_STADIUM_NAME, stadium.name)
                    putDouble(com.example.stadiumfind.features.booking.BookingFragment.ARG_PRICE_PER_HOUR, stadium.minPricePerHour)
                }
                findNavController().navigate(
                    R.id.action_stadiumDetailFragment_to_bookingFragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "Đang tải thông tin sân...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeDetailState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailState.collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            binding.layoutLoading.visibility = View.VISIBLE
                            binding.layoutError.visibility = View.GONE
                        }
                        is Resource.Success -> {
                            binding.layoutLoading.visibility = View.GONE
                            binding.layoutError.visibility = View.GONE
                            currentStadium = resource.data
                            bindStadiumData(resource.data)
                        }
                        is Resource.Error -> {
                            binding.layoutLoading.visibility = View.GONE
                            binding.layoutError.visibility = View.VISIBLE
                            binding.tvErrorMessage.text = resource.message ?: "Không tải được thông tin sân bóng"
                        }
                    }
                }
            }
        }
    }

    private fun bindStadiumData(stadium: StadiumItem) {
        with(binding) {
            tvDetailName.text = stadium.name
            tvDetailSport.text = stadium.primarySportBadge
            tvDetailRating.text = String.format(Locale.US, "%.1f", stadium.rating)
            tvDetailCourts.text = "• ${stadium.totalCourts} sân"
            tvDetailAddress.text = stadium.address
            tvDetailHours.text = "${stadium.formattedOperatingHours} hàng ngày"

            tvDetailDescription.text = if (!stadium.description.isNullOrBlank()) {
                stadium.description
            } else {
                "Sân bóng đá chất lượng cao, trang bị mặt cỏ nhân tạo tiêu chuẩn, hệ thống đèn chiếu sáng đêm hiện đại, bãi xe và khu vực nghỉ ngơi đầy đủ tiện ích."
            }

            tvDetailPrice.text = stadium.formattedPrice

            setupImageSlider(stadium.imageUrl)
        }
    }

    private fun setupImageSlider(imageUrl: String?) {
        // Tạo danh sách ảnh: nếu có ảnh từ server, ta đưa vào danh sách;
        // nếu không có, tạo 1 phần tử rỗng để adapter hiển thị placeholder.
        val images = if (!imageUrl.isNullOrBlank()) {
            listOf(imageUrl)
        } else {
            listOf("")
        }

        val adapter = StadiumImageSliderAdapter(images)
        binding.vpImageSlider.adapter = adapter

        setupDotsIndicator(images.size)

        // Hủy callback cũ nếu có để tránh memory leak
        pageChangeCallback?.let { binding.vpImageSlider.unregisterOnPageChangeCallback(it) }

        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotsIndicator(position, images.size)
            }
        }
        binding.vpImageSlider.registerOnPageChangeCallback(pageChangeCallback!!)
    }

    private fun setupDotsIndicator(count: Int) {
        val container = binding.dotsIndicator
        container.removeAllViews()

        if (count <= 1) {
            container.visibility = View.GONE
            return
        }

        container.visibility = View.VISIBLE
        val dotParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(6, 0, 6, 0)
        }

        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                layoutParams = dotParams
                setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (i == 0) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
                    )
                )
            }
            container.addView(dot)
        }
    }

    private fun updateDotsIndicator(selectedPos: Int, total: Int) {
        val container = binding.dotsIndicator
        if (total <= 1) return

        for (i in 0 until container.childCount) {
            val dot = container.getChildAt(i) as? ImageView ?: continue
            dot.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == selectedPos) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive
                )
            )
        }
    }

    override fun onDestroyView() {
        pageChangeCallback?.let {
            binding.vpImageSlider.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback = null
        _binding = null
        super.onDestroyView()
    }
}
