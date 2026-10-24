package com.example.stadiumfind.features.booking.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO gửi lên server để tạo đơn đặt sân.
 */
data class BookingCreateRequestDto(
    @SerializedName("courtId")
    val courtId: Long,

    @SerializedName("bookingDate")
    val bookingDate: String, // Định dạng "yyyy-MM-dd"

    @SerializedName("startTime")
    val startTime: String,   // Định dạng "HH:mm:ss"

    @SerializedName("endTime")
    val endTime: String,     // Định dạng "HH:mm:ss"

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("userId")
    val userId: Long? = null
)
