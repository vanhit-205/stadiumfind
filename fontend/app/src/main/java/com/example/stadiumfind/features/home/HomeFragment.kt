package com.example.stadiumfind.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.databinding.FragmentHomeBinding
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.presentation.adapter.StadiumAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var stadiumAdapter: StadiumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        loadMockStadiums()
    }

    private fun setupRecyclerView() {
        stadiumAdapter = StadiumAdapter(
            onItemClick = { stadium ->
                Toast.makeText(requireContext(), "Xem chi tiết: ${stadium.name}", Toast.LENGTH_SHORT).show()
            },
            onBookClick = { stadium ->
                Toast.makeText(requireContext(), "Đặt sân: ${stadium.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvStadiums.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stadiumAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupListeners() {
        binding.layoutSearchBar.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình tìm kiếm sân", Toast.LENGTH_SHORT).show()
        }

        binding.btnNotification.setOnClickListener {
            Toast.makeText(requireContext(), "Bạn không có thông báo mới", Toast.LENGTH_SHORT).show()
        }

        binding.btnBannerAction.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang mục Ghép Kèo Giao Hữu", Toast.LENGTH_SHORT).show()
        }

        binding.tvViewAll.setOnClickListener {
            Toast.makeText(requireContext(), "Xem tất cả sân bóng", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Dữ liệu mẫu (Mock data) trực quan phục vụ kiểm thử UI Ngày 12
     * (Sẽ được thay thế hoàn toàn bằng Paging 3 & API backend ở Ngày 13).
     */
    private fun loadMockStadiums() {
        val mockStadiums = listOf(
            StadiumItem(
                id = 1L,
                name = "Sân bóng đá Chuyên Việt",
                address = "Số 98 Cầu Giấy, Quan Hoa",
                district = "Cầu Giấy",
                provinceCity = "Hà Nội",
                openingTime = "06:00",
                closingTime = "23:00",
                imageUrl = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80",
                rating = 4.8,
                reviewCount = 56,
                minPricePerHour = 300000.0,
                sportTypes = listOf("Bóng đá • Sân 7")
            ),
            StadiumItem(
                id = 2L,
                name = "Sân cỏ nhân tạo PVF Mỹ Đình",
                address = "Lê Đức Thọ, Mỹ Đình 1",
                district = "Nam Từ Liêm",
                provinceCity = "Hà Nội",
                openingTime = "05:30",
                closingTime = "23:30",
                imageUrl = "https://images.unsplash.com/photo-1529900240041-22f1df4d0f11?auto=format&fit=crop&w=800&q=80",
                rating = 4.9,
                reviewCount = 120,
                minPricePerHour = 450000.0,
                sportTypes = listOf("Bóng đá • Sân 7 & 11")
            ),
            StadiumItem(
                id = 3L,
                name = "Sân bóng mini Đại Học Y",
                address = "Số 1 Tôn Thất Tùng, Kim Liên",
                district = "Đống Đa",
                provinceCity = "Hà Nội",
                openingTime = "06:00",
                closingTime = "22:00",
                imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
                rating = 4.6,
                reviewCount = 38,
                minPricePerHour = 250000.0,
                sportTypes = listOf("Bóng đá • Sân 5")
            ),
            StadiumItem(
                id = 4L,
                name = "Sân bóng đá Thành Lâm Green Park",
                address = "Khu đô thị Định Công",
                district = "Hoàng Mai",
                provinceCity = "Hà Nội",
                openingTime = "06:00",
                closingTime = "23:00",
                imageUrl = "https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=800&q=80",
                rating = 4.7,
                reviewCount = 84,
                minPricePerHour = 350000.0,
                sportTypes = listOf("Bóng đá • Sân 7")
            ),
            StadiumItem(
                id = 5L,
                name = "Sân bóng đá FPT Cầu Giấy",
                address = "Phố Duy Tân, Dịch Vọng Hậu",
                district = "Cầu Giấy",
                provinceCity = "Hà Nội",
                openingTime = "06:00",
                closingTime = "22:30",
                imageUrl = "https://images.unsplash.com/photo-1489944440615-453fc2b6a9a9?auto=format&fit=crop&w=800&q=80",
                rating = 4.5,
                reviewCount = 29,
                minPricePerHour = 280000.0,
                sportTypes = listOf("Bóng đá • Sân 5")
            )
        )

        stadiumAdapter.submitList(mockStadiums)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
