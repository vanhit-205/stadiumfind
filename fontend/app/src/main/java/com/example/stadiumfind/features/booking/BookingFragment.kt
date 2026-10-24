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
import com.example.stadiumfind.databinding.BottomSheetConfirmBookingBinding
import com.example.stadiumfind.databinding.BottomSheetSelectStadiumBinding
import com.example.stadiumfind.databinding.FragmentBookingBinding
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.presentation.BookingViewModel
import com.example.stadiumfind.features.booking.presentation.adapter.CalendarDayAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.CourtChipAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.StadiumPickerAdapter
import com.example.stadiumfind.features.booking.presentation.adapter.TimeSlotAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private var confirmBottomSheet: BottomSheetDialog? = null
    private var confirmSheetBinding: BottomSheetConfirmBookingBinding? = null

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
            showConfirmBookingBottomSheet()
        }
    }

    /**
     * Mở BottomSheet xác nhận đơn đặt sân trước khi gửi API (Ngày 22 - Booking Flow).
     */
    private fun showConfirmBookingBottomSheet() {
        val state = viewModel.uiState.value
        if (state.selectedTimeSlots.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn khung giờ trước khi đặt", Toast.LENGTH_SHORT).show()
            return
        }

        val bottomSheet = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetConfirmBookingBinding.inflate(layoutInflater)
        bottomSheet.setContentView(sheetBinding.root)
        confirmBottomSheet = bottomSheet
        confirmSheetBinding = sheetBinding

        // Điền dữ liệu tóm tắt
        sheetBinding.tvConfirmStadiumName.text = state.stadiumName
        sheetBinding.tvConfirmCourtName.text = state.selectedCourt?.name ?: "Sân con"
        sheetBinding.tvConfirmDate.text = state.selectedDate?.fullDisplayString ?: ""

        val slotRanges = state.selectedTimeSlots.joinToString("\n") {
            "• ${it.displayRange} (${it.formattedPrice})"
        }
        sheetBinding.tvConfirmTimeSlots.text = slotRanges
        sheetBinding.tvConfirmTotalPrice.text = state.formattedEstimatedPrice

        if (state.note.isNotBlank()) {
            sheetBinding.layoutConfirmNote.visibility = View.VISIBLE
            sheetBinding.tvConfirmNote.text = state.note
        } else {
            sheetBinding.layoutConfirmNote.visibility = View.GONE
        }

        sheetBinding.btnSubmitBookingNow.setOnClickListener {
            viewModel.submitBooking()
        }

        sheetBinding.btnCancelConfirm.setOnClickListener {
            bottomSheet.dismiss()
        }

        bottomSheet.setOnDismissListener {
            confirmBottomSheet = null
            confirmSheetBinding = null
        }

        bottomSheet.show()
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
                    handleBookingProgress(state)
                    handleBookingResult(state)
                }
            }
        }
    }

    private fun handleBookingProgress(state: com.example.stadiumfind.features.booking.presentation.BookingUiState) {
        confirmSheetBinding?.let { sheetBinding ->
            if (state.isBookingInProgress) {
                sheetBinding.btnSubmitBookingNow.isEnabled = false
                sheetBinding.btnSubmitBookingNow.text = ""
                sheetBinding.pbConfirmLoading.visibility = View.VISIBLE
                sheetBinding.btnCancelConfirm.isEnabled = false
            } else {
                sheetBinding.btnSubmitBookingNow.isEnabled = true
                sheetBinding.btnSubmitBookingNow.text = "Xác Nhận Đặt Sân Ngay"
                sheetBinding.pbConfirmLoading.visibility = View.GONE
                sheetBinding.btnCancelConfirm.isEnabled = true
            }
        }
    }

    private fun handleBookingResult(state: com.example.stadiumfind.features.booking.presentation.BookingUiState) {
        // Xử lý khi đặt sân thành công
        state.bookingSuccessResult?.let { result ->
            confirmBottomSheet?.dismiss()
            confirmBottomSheet = null

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("🎉 Đặt Sân Thành Công!")
                .setMessage(
                    "Mã đặt sân: #${result.id}\n" +
                    "Cụm sân: ${result.stadiumName}\n" +
                    "Sân con: ${result.courtName}\n" +
                    "Ngày đá: ${result.bookingDate}\n" +
                    "Khung giờ: ${result.displayTimeRange}\n" +
                    "Tổng tiền: ${result.formattedTotalPrice}\n\n" +
                    "Đơn đặt của bạn đã được ghi nhận trên hệ thống!"
                )
                .setPositiveButton("Xem Vé / Mã QR") { dialog, _ ->
                    dialog.dismiss()
                    // Sẵn sàng chuyển tiếp sang màn hình Mã QR ở Ngày 23
                    Toast.makeText(
                        requireContext(),
                        "Vé #${result.id} đã sẵn sàng cho Ngày 23 (Mã QR)",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setNegativeButton("Đóng") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()

            viewModel.clearBookingSuccess()
        }

        // Xử lý khi có thông báo lỗi từ server (Ví dụ: trùng lịch 409)
        state.bookingErrorMessage?.let { errorMsg ->
            confirmBottomSheet?.dismiss()
            confirmBottomSheet = null

            val title = if (state.isConflictError) "⚠️ Trùng Lịch Đặt Sân" else "❌ Đặt Sân Thất Bại"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(errorMsg)
                .setPositiveButton("Đã hiểu") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

            viewModel.clearBookingError()
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
        confirmBottomSheet?.dismiss()
        confirmBottomSheet = null
        confirmSheetBinding = null
        _binding = null
        super.onDestroyView()
    }
}
