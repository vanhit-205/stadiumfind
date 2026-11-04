package com.example.stadiumfind.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation
import com.example.stadiumfind.R
import com.example.stadiumfind.features.profile.presentation.ProfileUiState
import com.example.stadiumfind.features.profile.presentation.ProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var ivAvatar: ImageView
    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvTotalBookingsCount: TextView
    private lateinit var tvMembershipTier: TextView
    private lateinit var cardMyBookings: MaterialCardView
    private lateinit var btnNotificationSetting: View
    private lateinit var btnAboutApp: View
    private lateinit var btnLogout: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupListeners()
        observeData()
    }

    private fun initViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        ivAvatar = view.findViewById(R.id.ivAvatar)
        tvFullName = view.findViewById(R.id.tvFullName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvTotalBookingsCount = view.findViewById(R.id.tvTotalBookingsCount)
        tvMembershipTier = view.findViewById(R.id.tvMembershipTier)
        cardMyBookings = view.findViewById(R.id.cardMyBookings)
        btnNotificationSetting = view.findViewById(R.id.btnNotificationSetting)
        btnAboutApp = view.findViewById(R.id.btnAboutApp)
        btnLogout = view.findViewById(R.id.btnLogout)

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_400)
    }

    private fun setupListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadProfile()
        }

        // Chuyển sang màn hình "Vé đặt sân của tôi" (My Bookings)
        cardMyBookings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myBookingsFragment)
        }

        // Chuyển sang màn hình "Kèo tôi tham gia" (My Matches)
        val cardMyMatches = requireView().findViewById<MaterialCardView>(R.id.cardMyMatches)
        cardMyMatches?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myMatchesFragment)
        }

        btnNotificationSetting.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationFragment)
        }

        btnAboutApp.setOnClickListener {
            AlertDialog.Builder(requireContext(), R.style.Theme_StadiumFind)
                .setTitle("StadiumFind v1.0")
                .setMessage("Ứng dụng tìm kiếm và đặt sân bóng đá tiện lợi hàng đầu Việt Nam.\n\nPhát triển bởi Clean Architecture + Spring Boot.")
                .setPositiveButton("Đã hiểu", null)
                .show()
        }

        // Đăng xuất: Hiện dialog xác nhận
        btnLogout.setOnClickListener {
            showLogoutConfirmDialog()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ProfileUiState.Loading -> {
                            // Chỉ quay spinner khi swipe refresh
                        }
                        is ProfileUiState.Success -> {
                            swipeRefreshLayout.isRefreshing = false
                            val user = state.user
                            tvFullName.text = user.fullName
                            tvEmail.text = user.email
                            tvPhone.text = if (user.phoneNumber.isNotBlank()) user.phoneNumber else "Chưa cập nhật SĐT"
                            tvTotalBookingsCount.text = user.totalBookings.toString()

                            // Đặt cấp bậc thành viên dựa trên số lần đặt
                            tvMembershipTier.text = when {
                                user.totalBookings >= 10 -> "Hạng Kim Cương 💎"
                                user.totalBookings >= 5 -> "Hạng Vàng 🥇"
                                user.totalBookings >= 2 -> "Hạng Bạc 🥈"
                                else -> "Thành Viên Mới ⚽"
                            }

                            if (!user.avatarUrl.isNullOrBlank()) {
                                ivAvatar.load(user.avatarUrl) {
                                    crossfade(true)
                                    transformations(CircleCropTransformation())
                                    placeholder(R.drawable.ic_person)
                                    error(R.drawable.ic_person)
                                }
                            } else {
                                ivAvatar.setImageResource(R.drawable.ic_person)
                            }
                        }
                        is ProfileUiState.Error -> {
                            swipeRefreshLayout.isRefreshing = false
                        }
                        is ProfileUiState.Idle -> {
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đăng Xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản không?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                viewModel.logout {
                    findNavController().navigate(R.id.action_global_loginFragment)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
