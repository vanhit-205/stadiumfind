package com.example.stadiumfind.features.booking.presentation.mybookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.FragmentBookingListTabBinding
import com.example.stadiumfind.features.booking.presentation.adapter.BookingTicketAdapter
import com.example.stadiumfind.features.booking.presentation.success.BookingSuccessFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingListTabFragment : Fragment() {

    private var _binding: FragmentBookingListTabBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyBookingsViewModel by activityViewModels()
    private lateinit var adapter: BookingTicketAdapter

    private var tabType: Int = TAB_UPCOMING

    companion object {
        const val ARG_TAB_TYPE = "arg_tab_type"
        const val TAB_UPCOMING = 0
        const val TAB_HISTORY = 1

        fun newInstance(tabType: Int): BookingListTabFragment {
            return BookingListTabFragment().apply {
                arguments = bundleOf(ARG_TAB_TYPE to tabType)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabType = arguments?.getInt(ARG_TAB_TYPE, TAB_UPCOMING) ?: TAB_UPCOMING
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingListTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = BookingTicketAdapter(
            onTicketClick = { ticket ->
                navigateToTicketDetail(ticket)
            },
            onQrClick = { ticket ->
                navigateToTicketDetail(ticket)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setColorSchemeResources(R.color.primary_400)
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadBookings()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MyBookingsUiState.Loading -> {
                            if (!binding.swipeRefreshLayout.isRefreshing) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            binding.layoutEmpty.visibility = View.GONE
                        }
                        is MyBookingsUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false

                            val list = if (tabType == TAB_UPCOMING) state.upcoming else state.history
                            adapter.submitList(list)

                            if (list.isEmpty()) {
                                binding.layoutEmpty.visibility = View.VISIBLE
                                if (tabType == TAB_UPCOMING) {
                                    binding.tvEmptyTitle.text = "Chưa có trận nào sắp diễn ra"
                                    binding.tvEmptySubtitle.text = "Hãy chọn sân bóng và khung giờ để lên lịch thi đấu nhé!"
                                } else {
                                    binding.tvEmptyTitle.text = "Lịch sử trận đấu trống"
                                    binding.tvEmptySubtitle.text = "Các trận đấu đã qua sẽ được lưu giữ tại đây."
                                }
                            } else {
                                binding.layoutEmpty.visibility = View.GONE
                            }
                        }
                        is MyBookingsUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            if (adapter.currentList.isEmpty()) {
                                binding.layoutEmpty.visibility = View.VISIBLE
                                binding.tvEmptyTitle.text = "Không thể tải danh sách vé"
                                binding.tvEmptySubtitle.text = state.message
                            }
                        }
                        is MyBookingsUiState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun navigateToTicketDetail(ticket: com.example.stadiumfind.features.booking.domain.model.BookingResult) {
        val bundle = bundleOf(
            BookingSuccessFragment.ARG_TICKET_ID to ticket.id,
            BookingSuccessFragment.ARG_STADIUM_NAME to ticket.stadiumName,
            BookingSuccessFragment.ARG_COURT_NAME to ticket.courtName,
            BookingSuccessFragment.ARG_COURT_ID to ticket.courtId,
            BookingSuccessFragment.ARG_STADIUM_ADDRESS to (ticket.stadiumAddress ?: "Địa chỉ sân bóng"),
            BookingSuccessFragment.ARG_BOOKING_DATE to ticket.bookingDate,
            BookingSuccessFragment.ARG_TIME_RANGE to ticket.displayTimeRange,
            BookingSuccessFragment.ARG_TOTAL_PRICE to ticket.formattedTotalPrice,
            BookingSuccessFragment.ARG_NOTE to ticket.note
        )
        findNavController().navigate(R.id.bookingSuccessFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
