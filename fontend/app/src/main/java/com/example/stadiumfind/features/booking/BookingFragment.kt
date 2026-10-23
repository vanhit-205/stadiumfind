package com.example.stadiumfind.features.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.R
import com.example.stadiumfind.databinding.FragmentBookingBinding
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.presentation.BookingViewModel
import com.example.stadiumfind.features.booking.presentation.adapter.CalendarDayAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.CourtChipAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.TimeSlotAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Màn hình Đặt Sân / Chọn Ngày & Khung Giờ (BookingTimeSelectionFragment).
 * Tích hợp Custom Horizontal Calendar, Toggle Ca 60p vs 90p, Time Slot Grid và Court Selector.
 */
@AndroidEntryPoint
class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()

    private lateinit var courtChipAdapter: CourtChipAdapter
    private lateinit var calendarDayAdapter: CalendarDayAdapter
    private lateinit var timeSlotAdapter: TimeSlotAdapter

    companion object {
        const val ARG_STADIUM_ID = "stadiumId"
        const val ARG_STADIUM_NAME = "stadiumName"
        const val ARG_PRICE_PER_HOUR = "pricePerHour"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        extractArgumentsAndInit()
        setupAdapters()
        setupListeners()
        observeState()
    }

    private fun extractArgumentsAndInit() {
        val stadiumId = arguments?.getLong(ARG_STADIUM_ID, 1L) ?: 1L
        val stadiumName = arguments?.getString(ARG_STADIUM_NAME) ?: "Sân bóng Chuyên Việt"
        val pricePerHour = arguments?.getDouble(ARG_PRICE_PER_HOUR, 200000.0) ?: 200000.0

        viewModel.initBooking(stadiumId, stadiumName, pricePerHour)
    }

    private fun setupAdapters() {
        courtChipAdapter = CourtChipAdapter { court ->
            viewModel.selectCourt(court)
        }
        binding.rvCourts.adapter = courtChipAdapter

        calendarDayAdapter = CalendarDayAdapter { day ->
            viewModel.selectDate(day)
        }
        binding.rvCalendarDays.adapter = calendarDayAdapter

        timeSlotAdapter = TimeSlotAdapter { slot ->
            viewModel.selectTimeSlot(slot)
        }
        binding.rvTimeSlots.adapter = timeSlotAdapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            if (!findNavController().popBackStack()) {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        // Toggle Ca 60 phút
        binding.btnDuration60.setOnClickListener {
            viewModel.selectDuration(SlotDuration.DURATION_60_MIN)
        }

        // Toggle Ca 90 phút
        binding.btnDuration90.setOnClickListener {
            viewModel.selectDuration(SlotDuration.DURATION_90_MIN)
        }

        // Note input
        binding.etBookingNote.doAfterTextChanged { editable ->
            viewModel.updateNote(editable?.toString().orEmpty())
        }

        // Nút Xác nhận & Tiếp tục đặt sân
        binding.btnConfirmBooking.setOnClickListener {
            val state = viewModel.uiState.value
            val court = state.selectedCourt?.name ?: "Sân con"
            val date = state.selectedDate?.fullDisplayString ?: ""
            val slot = state.selectedTimeSlot?.displayRange ?: ""
            val price = state.formattedEstimatedPrice

            val message = "✅ Đã chọn $court\n📅 $date\n⏰ Ca: $slot\n💰 Tạm tính: $price"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
        }
    }

    private fun renderUiState(state: com.example.stadiumfind.features.booking.presentation.BookingUiState) {
        val context = requireContext()

        // Cập nhật Header
        binding.tvStadiumNameHeader.text = state.stadiumName
        binding.tvPriceHint.text = "Giá gốc: ${state.formattedBasePrice}"

        // Cập nhật Lịch ngày
        state.selectedDate?.let { date ->
            binding.tvCurrentMonthYear.text = "Tháng ${date.month}, ${date.year}"
            binding.tvSelectedDateDisplay.text = "📅 ${date.fullDisplayString}"
        }

        // Cập nhật Adapters
        courtChipAdapter.submitList(state.courts)
        calendarDayAdapter.submitList(state.days)
        timeSlotAdapter.submitList(state.timeSlots)

        // Cập nhật Duration Toggle
        if (state.selectedDuration == SlotDuration.DURATION_60_MIN) {
            binding.btnDuration60.setBackgroundResource(R.drawable.bg_duration_tab_active)
            binding.btnDuration60.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))

            binding.btnDuration90.setBackgroundResource(R.drawable.bg_duration_tab_inactive)
            binding.btnDuration90.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))

            binding.tvDurationSummaryBadge.text = "• Ca 60p"
        } else {
            binding.btnDuration60.setBackgroundResource(R.drawable.bg_duration_tab_inactive)
            binding.btnDuration60.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))

            binding.btnDuration90.setBackgroundResource(R.drawable.bg_duration_tab_active)
            binding.btnDuration90.setTextColor(ContextCompat.getColor(context, R.color.text_on_primary))

            binding.tvDurationSummaryBadge.text = "• Ca 90p"
        }

        // Cập nhật Bottom Summary Bar
        if (state.selectedTimeSlot != null) {
            val courtName = state.selectedCourt?.name ?: "Sân con"
            binding.tvSlotSummaryText.text = "$courtName • ${state.selectedTimeSlot.displayRange}"
            binding.tvTotalPrice.text = state.formattedEstimatedPrice
            binding.btnConfirmBooking.isEnabled = true
            binding.btnConfirmBooking.alpha = 1.0f
        } else {
            binding.tvSlotSummaryText.text = "Vui lòng chọn khung giờ trống"
            binding.tvTotalPrice.text = "0đ"
            binding.btnConfirmBooking.isEnabled = false
            binding.btnConfirmBooking.alpha = 0.5f
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
