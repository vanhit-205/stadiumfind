package com.example.stadiumfind.features.booking.presentation

import com.example.stadiumfind.features.booking.domain.model.CalendarDay
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.domain.model.TimeSlot
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * UI State cho màn hình Chọn Ngày & Khung Giờ Đặt Sân (BookingTimeSelection).
 * Hỗ trợ chọn Cụm sân, Sân con và Đặt nhiều ca đá cùng lúc (Multi-slot booking).
 */
data class BookingUiState(
    // Quản lý Cụm sân (Stadium)
    val stadiumId: Long = 0L,
    val stadiumName: String = "Sân bóng đá",
    val stadiumAddress: String = "",
    val basePricePerHour: Double = 200000.0,
    val stadiums: List<StadiumItem> = emptyList(),
    val selectedStadium: StadiumItem? = null,
    val isStadiumsLoading: Boolean = false,

    // Quản lý Sân con (Court)
    val courts: List<CourtItem> = emptyList(),
    val selectedCourt: CourtItem? = null,
    val isCourtsLoading: Boolean = false,

    // Quản lý Ngày & Thời lượng ca
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: CalendarDay? = null,
    val selectedDuration: SlotDuration = SlotDuration.DURATION_90_MIN,

    // Quản lý Khung giờ (Hỗ trợ chọn nhiều ca đá)
    val timeSlots: List<TimeSlot> = emptyList(),
    val selectedTimeSlots: List<TimeSlot> = emptyList(),
    val occupiedSlots: List<OccupiedSlot> = emptyList(),
    val isSlotsLoading: Boolean = false,

    // Ghi chú & Giá
    val note: String = "",
    val estimatedPrice: Double = 0.0,
    val isConfirmEnabled: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * Khung giờ đầu tiên được chọn (hỗ trợ tương thích ngược).
     */
    val selectedTimeSlot: TimeSlot?
        get() = selectedTimeSlots.firstOrNull()

    /**
     * Số lượng ca đá đang được chọn.
     */
    val selectedSlotsCount: Int
        get() = selectedTimeSlots.size

    /**
     * Chuỗi tóm tắt các ca đá đã chọn.
     */
    val formattedSelectedSlotsSummary: String
        get() {
            if (selectedTimeSlots.isEmpty()) {
                return "Vui lòng chọn khung giờ trống"
            }
            val courtName = selectedCourt?.name ?: "Sân con"
            return if (selectedTimeSlots.size == 1) {
                "$courtName • ${selectedTimeSlots.first().displayRange}"
            } else {
                val ranges = selectedTimeSlots.joinToString(", ") { it.displayRange }
                "$courtName • ${selectedTimeSlots.size} ca: $ranges"
            }
        }

    /**
     * Nhãn nút xác nhận theo số lượng ca.
     */
    val confirmButtonText: String
        get() {
            return if (selectedTimeSlots.size > 1) {
                "Đặt ${selectedTimeSlots.size} ca • $formattedEstimatedPrice"
            } else {
                "Xác Nhận & Tiếp Tục"
            }
        }

    val formattedEstimatedPrice: String
        get() {
            if (estimatedPrice <= 0.0) return "0đ"
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
            }
            val formatter = DecimalFormat("#,###", symbols)
            return "${formatter.format(estimatedPrice)}đ"
        }

    val formattedBasePrice: String
        get() {
            if (basePricePerHour <= 0.0) return "Liên hệ"
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
            }
            val formatter = DecimalFormat("#,###", symbols)
            return "${formatter.format(basePricePerHour)}đ / giờ"
        }
}
