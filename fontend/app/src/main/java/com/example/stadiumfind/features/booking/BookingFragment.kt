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
import com.example.stadiumfind.databinding.BottomSheetSelectStadiumBinding
import com.example.stadiumfind.databinding.FragmentBookingBinding
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.presentation.BookingViewModel
import com.example.stadiumfind.features.booking.presentation.adapter.CalendarDayAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.CourtChipAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.StadiumPickerAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.TimeSlotAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Màn hình Đặt Sân / Chọn Cụm Sân, Sân Con, Ngày & Khung Giờ (BookingTimeSelectionFragment).
 * Hỗ trợ chọn Cụm Sân (Stadium Picker), chọn nhiều ca đá cùng lúc (Multi-slot selection).
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
        val stadiumName = arguments?.getString(ARG_STADIUM_NAME) ?: "Sân bóng Thượng Đình"
        val pricePerHour = arguments?.getDouble(ARG_PRICE_PER_HOUR, 250000.0) ?: 250000.0

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

        // Chọn / Đổi Cụm Sân bóng
        binding.btnChangeStadium.setOnClickListener {
            showStadiumPickerBottomSheet()
        }
        binding.cardSelectedStadium.setOnClickListener {
            showStadiumPickerBottomSheet()
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

        // Nút Xác nhận & Tiếp tục đặt sân (hỗ trợ nhiều ca đá)
        binding.btnConfirmBooking.setOnClickListener {
            val state = viewModel.uiState.value
            val stadium = state.stadiumName
            val court = state.selectedCourt?.name ?: "Sân con"
            val date = state.selectedDate?.fullDisplayString ?: ""
            val count = state.selectedSlotsCount
            val price = state.formattedEstimatedPrice

            val slotDetails = state.selectedTimeSlots.joinToString("\n") {
                "  • ${it.displayRange} (${it.formattedPrice})"
            }

            val message = "✅ Đã chọn cụm: $stadium\n🏟️ Sân: $court\n📅 $date\n⏰ Số ca đã chọn: $count ca:\n$slotDetails\n💰 Tổng tạm tính: $price"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Mở BottomSheet cho phép người dùng tìm kiếm và chọn Cụm sân bóng.
     */
    private fun showStadiumPickerBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetSelectStadiumBinding.inflate(layoutInflater)
        bottomSheet.setContentView(sheetBinding.root)

        val pickerAdapter = StadiumPickerAdapter { selectedStadium ->
            viewModel.selectStadium(selectedStadium)
            bottomSheet.dismiss()
        }

        sheetBinding.rvSelectStadiums.adapter = pickerAdapter
        val allStadiums = viewModel.uiState.value.stadiums
        pickerAdapter.submitList(allStadiums)
        pickerAdapter.setSelectedId(viewModel.uiState.value.stadiumId)

        if (allStadiums.isEmpty()) {
            sheetBinding.pbLoadingStadiums.visibility = View.VISIBLE
            viewModel.loadStadiums()
        } else {
            sheetBinding.pbLoadingStadiums.visibility = View.GONE
        }

        // Tìm kiếm nhanh cụm sân theo tên hoặc địa chỉ
        sheetBinding.etSearchStadium.doAfterTextChanged { editable ->
            val query = editable?.toString()?.trim()?.lowercase().orEmpty()
            if (query.isEmpty()) {
                pickerAdapter.submitList(allStadiums)
            } else {
                val filtered = allStadiums.filter {
                    it.name.lowercase().contains(query) || it.address.lowercase().contains(query)
                }
                pickerAdapter.submitList(filtered)
            }
        }

        bottomSheet.show()
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

        // 1. Cập nhật Thông tin Cụm Sân bóng
        binding.tvStadiumNameHeader.text = state.stadiumName
        binding.tvPriceHint.text = "Giá gốc: ${state.formattedBasePrice}"

        binding.tvSelectedStadiumName.text = state.stadiumName
        binding.tvSelectedStadiumAddress.text = if (state.stadiumAddress.isNotBlank()) {
            state.stadiumAddress
        } else {
            "Nhấn để xem địa chỉ chi tiết"
        }
        binding.tvSelectedStadiumPrice.text = "Giá gốc: ${state.formattedBasePrice}"

        // 2. Cập nhật Lịch ngày
        state.selectedDate?.let { date ->
            binding.tvCurrentMonthYear.text = "Tháng ${date.month}, ${date.year}"
            binding.tvSelectedDateDisplay.text = "📅 ${date.fullDisplayString}"
        }

        // 3. Cập nhật Adapters
        courtChipAdapter.submitList(state.courts)
        calendarDayAdapter.submitList(state.days)
        timeSlotAdapter.submitList(state.timeSlots)

        // 4. Cập nhật Loading Indicator cho khung giờ
        if (state.isSlotsLoading) {
            binding.pbLoadingSlots.visibility = View.VISIBLE
            binding.rvTimeSlots.alpha = 0.5f
        } else {
            binding.pbLoadingSlots.visibility = View.GONE
            binding.rvTimeSlots.alpha = 1.0f
        }

        // 5. Cập nhật Duration Toggle (Ca 60p vs 90p)
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

        // 6. Cập nhật Bottom Summary Bar (Hỗ trợ nhiều ca đá)
        if (state.selectedTimeSlots.isNotEmpty()) {
            binding.tvSlotSummaryText.text = state.formattedSelectedSlotsSummary
            binding.tvTotalPrice.text = state.formattedEstimatedPrice
            binding.btnConfirmBooking.isEnabled = true
            binding.btnConfirmBooking.alpha = 1.0f
            binding.btnConfirmBooking.text = state.confirmButtonText
        } else {
            binding.tvSlotSummaryText.text = "Vui lòng chọn khung giờ trống"
            binding.tvTotalPrice.text = "0đ"
            binding.btnConfirmBooking.isEnabled = false
            binding.btnConfirmBooking.alpha = 0.5f
            binding.btnConfirmBooking.text = "Tiếp Tục Đặt"
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
