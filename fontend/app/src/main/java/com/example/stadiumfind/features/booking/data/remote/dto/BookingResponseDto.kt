package com.example.stadiumfind.features.booking.data.remote.dto

import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.google.gson.annotations.SerializedName

/**
 * DTO nhận dữ liệu phản hồi sau khi tạo hoặc truy vấn đơn đặt sân từ backend.
 */
data class BookingResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("userName")
    val userName: String? = null,

    @SerializedName("courtId")
    val courtId: Long,

    @SerializedName("courtName")
    val courtName: String? = null,

    @SerializedName("stadiumId")
    val stadiumId: Long? = null,

    @SerializedName("stadiumName")
    val stadiumName: String? = null,

    @SerializedName("bookingDate")
    val bookingDate: String,

    @SerializedName("startTime")
    val startTime: String,

    @SerializedName("endTime")
    val endTime: String,

    @SerializedName("totalPrice")
    val totalPrice: Double? = 0.0,

    @SerializedName("status")
    val status: String? = "PENDING",

    @SerializedName("paymentStatus")
    val paymentStatus: String? = "UNPAID",

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    fun toDomain(): BookingResult {
        return BookingResult(
            id = id,
            userId = userId,
            userName = userName,
            courtId = courtId,
            courtName = courtName ?: "Sân bóng",
            stadiumId = stadiumId,
            stadiumName = stadiumName ?: "Cụm sân",
            bookingDate = bookingDate,
            startTime = startTime,
            endTime = endTime,
            totalPrice = totalPrice ?: 0.0,
            status = status ?: "PENDING",
            paymentStatus = paymentStatus ?: "UNPAID",
            note = note,
            createdAt = createdAt
        )
    }
}
