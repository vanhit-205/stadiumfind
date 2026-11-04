package com.example.stadiumfind.features.booking.domain.model

/**
 * Model đại diện cho một khung giờ đã có người đặt trước trả về từ server.
 */
data class OccupiedSlot(
    val courtId: Long,
    val bookingDate: String, // "YYYY-MM-DD"
    val startTime: String,   // "HH:mm" hoặc "HH:mm:ss"
    val endTime: String,     // "HH:mm" hoặc "HH:mm:ss"
    val status: String? = null
) {
    val startMinutes: Int
        get() = parseTimeToMinutes(startTime)

    val endMinutes: Int
        get() = parseTimeToMinutes(endTime)

    companion object {
        fun parseTimeToMinutes(timeStr: String): Int {
            val parts = timeStr.trim().split(":")
            if (parts.size >= 2) {
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0
                return hour * 60 + minute
            }
            return 0
        }
    }
}
