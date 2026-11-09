package com.example.stadiumfind.features.booking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity lưu trữ thông tin vé đặt sân vào SQLite cục bộ (Room Database).
 * Cho phép người dùng truy cập vé và hiển thị mã QR check-in khi mất kết nối mạng (Offline).
 */
@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val id: Long,
    val userId: Long?,
    val userName: String?,
    val courtId: Long,
    val courtName: String,
    val stadiumId: Long?,
    val stadiumName: String,
    val stadiumAddress: String?,
    val stadiumImageUrl: String?,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Double,
    val status: String,
    val paymentStatus: String,
    val note: String?,
    val qrCodeData: String?,
    val createdAt: String?
)
