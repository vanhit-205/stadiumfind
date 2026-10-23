package com.example.stadiumfind.features.booking.presentation

import com.example.stadiumfind.features.booking.domain.model.CalendarDay
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.SlotDuration
import com.example.stadiumfind.features.booking.domain.model.TimeSlot
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * UI State cho màn hình Chọn Ngày & Khung Giờ Đặt Sân (BookingTimeSelection).
 */
data class BookingUiState(
    val stadiumId: Long = 0L,
    val stadiumName: String = "Sân bóng đá",
    val stadiumAddress: String = "",
    val basePricePerHour: Double = 200000.0,
    val selectedDuration: SlotDuration = SlotDuration.DURATION_90_MIN,
    val courts: List<CourtItem> = emptyList(),
    val selectedCourt: CourtItem? = null,
    val days: List<CalendarDay> = emptyList(),
    val selectedDate: CalendarDay? = null,
    val timeSlots: List<TimeSlot> = emptyList(),
    val selectedTimeSlot: TimeSlot? = null,
    val note: String = "",
    val estimatedPrice: Double = 0.0,
    val isConfirmEnabled: Boolean = false
) {
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
