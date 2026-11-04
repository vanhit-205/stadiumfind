package com.example.stadiumfind.features.booking.data.remote.dto

import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.google.gson.annotations.SerializedName

/**
 * DTO nhận dữ liệu khung giờ đã đặt từ backend:
 * {
 *   "courtId": 1,
 *   "bookingDate": "2026-10-23",
 *   "startTime": "17:30:00",
 *   "endTime": "19:00:00",
 *   "status": "CONFIRMED"
 * }
 */
data class OccupiedSlotDto(
    @SerializedName("courtId")
    val courtId: Long,
    @SerializedName("bookingDate")
    val bookingDate: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("status")
    val status: String? = null
) {
    fun toDomain(): OccupiedSlot {
        return OccupiedSlot(
            courtId = courtId,
            bookingDate = bookingDate,
            startTime = startTime,
            endTime = endTime,
            status = status
        )
    }
}
