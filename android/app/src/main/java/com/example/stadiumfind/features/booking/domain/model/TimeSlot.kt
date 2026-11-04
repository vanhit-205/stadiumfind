package com.example.stadiumfind.features.booking.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Model biểu diễn một khung giờ / ca đá bóng.
 */
data class TimeSlot(
    val id: String,
    val startTime: String,      // Ví dụ: "17:30"
    val endTime: String,        // Ví dụ: "19:00"
    val duration: SlotDuration, // 60 phút hoặc 90 phút
    val startHour: Int,         // 17
    val startMinute: Int,       // 30
    val status: TimeSlotStatus = TimeSlotStatus.AVAILABLE,
    val basePrice: Double = 0.0 // Giá tương ứng với ca này
) {
    val displayRange: String
        get() = "$startTime - $endTime"

    val isPeakHour: Boolean
        get() = startHour in 17..21 // Ca tối thường là giờ vàng

    val formattedPrice: String
        get() {
            if (basePrice <= 0) return ""
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
            }
            val formatter = DecimalFormat("#,###", symbols)
            return "${formatter.format(basePrice)}đ"
        }
}
