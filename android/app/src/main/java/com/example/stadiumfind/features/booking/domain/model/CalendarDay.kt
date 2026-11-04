package com.example.stadiumfind.features.booking.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Model biểu diễn một ô ngày trên thanh lịch ngang.
 */
data class CalendarDay(
    val dateMillis: Long,
    val dayOfWeek: String,       // "Th 2", "Th 3", ..., "CN" hoặc "Hôm nay"
    val dayOfMonth: Int,         // 22
    val month: Int,              // 10
    val year: Int,               // 2026
    val isToday: Boolean = false,
    val isSelected: Boolean = false
) {
    /**
     * Chuỗi định dạng YYYY-MM-DD để gửi lên API backend (ví dụ: "2026-10-23")
     */
    val apiDateString: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(dateMillis))
        }

    /**
     * Chuỗi hiển thị đầy đủ (ví dụ: "Thứ Sáu, 23/10/2026")
     */
    val fullDisplayString: String
        get() {
            val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
            val dow = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Thứ Hai"
                Calendar.TUESDAY -> "Thứ Ba"
                Calendar.WEDNESDAY -> "Thứ Tư"
                Calendar.THURSDAY -> "Thứ Năm"
                Calendar.FRIDAY -> "Thứ Sáu"
                Calendar.SATURDAY -> "Thứ Bảy"
                Calendar.SUNDAY -> "Chủ Nhật"
                else -> ""
            }
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateFormatted = sdf.format(Date(dateMillis))
            return if (isToday) "$dow (Hôm nay), $dateFormatted" else "$dow, $dateFormatted"
        }
}
