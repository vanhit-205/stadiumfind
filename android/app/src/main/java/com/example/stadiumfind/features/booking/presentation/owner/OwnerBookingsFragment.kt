package com.example.stadiumfind.features.booking.presentation.owner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.databinding.FragmentOwnerBookingsBinding
import com.example.stadiumfind.features.booking.presentation.adapter.OwnerBookingAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OwnerBookingsFragment : Fragment() {

    private var _binding: FragmentOwnerBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OwnerBookingsViewModel by viewModels()
    private lateinit var adapter: OwnerBookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnerBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = OwnerBookingAdapter(
            onApproveClick = { booking ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Phê Duyệt Đơn Đặt Sân")
                    .setMessage("Bạn có chắc chắn muốn duyệt đơn #${booking.id} cho sân ${booking.courtName} lúc ${booking.displayTimeRange} ngày ${booking.bookingDate}?")
                    .setPositiveButton("Duyệt ngay") { _, _ ->
                        viewModel.approveBooking(booking.id)
                    }
                    .setNegativeButton("Hủy", null)
                    .show()
            },
            onRejectClick = { booking ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Từ Chối Đơn Đặt Sân")
                    .setMessage("Bạn có chắc chắn muốn từ chối đơn #${booking.id}?")
                    .setPositiveButton("Từ chối") { _, _ ->
                        viewModel.rejectBooking(booking.id)
                    }
                    .setNegativeButton("Đóng", null)
                    .show()
            }
        )
        binding.rvOwnerBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOwnerBookings.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshOwner.setOnRefreshListener {
            viewModel.loadOwnerBookings()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is OwnerBookingsUiState.Loading -> {
                                if (!binding.swipeRefreshOwner.isRefreshing) {
                                    binding.pbOwnerLoading.visibility = View.VISIBLE
                                }
                                binding.layoutOwnerEmpty.visibility = View.GONE
                            }
                            is OwnerBookingsUiState.Success -> {
                                binding.pbOwnerLoading.visibility = View.GONE
                                binding.swipeRefreshOwner.isRefreshing = false
                                adapter.submitList(state.bookings)
                                if (state.bookings.isEmpty()) {
                                    binding.layoutOwnerEmpty.visibility = View.VISIBLE
                                } else {
                                    binding.layoutOwnerEmpty.visibility = View.GONE
                                }
                            }
                            is OwnerBookingsUiState.Error -> {
                                binding.pbOwnerLoading.visibility = View.GONE
                                binding.swipeRefreshOwner.isRefreshing = false
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                            is OwnerBookingsUiState.Idle -> {
                                binding.pbOwnerLoading.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.actionMessage.collect { msg ->
                        msg?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            viewModel.clearActionMessage()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
