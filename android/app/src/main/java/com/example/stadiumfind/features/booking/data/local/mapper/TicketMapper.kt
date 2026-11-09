package com.example.stadiumfind.features.booking.data.local.mapper

import com.example.stadiumfind.features.booking.data.local.entity.TicketEntity
import com.example.stadiumfind.features.booking.domain.model.BookingResult

/**
 * Mappers chuyển đổi qua lại giữa TicketEntity (Room SQLite) và BookingResult (Domain Model).
 */
fun TicketEntity.toDomain(): BookingResult {
    return BookingResult(
        id = id,
        userId = userId,
        userName = userName,
        courtId = courtId,
        courtName = courtName,
        stadiumId = stadiumId,
        stadiumName = stadiumName,
        stadiumAddress = stadiumAddress,
        stadiumImageUrl = stadiumImageUrl,
        bookingDate = bookingDate,
        startTime = startTime,
        endTime = endTime,
        totalPrice = totalPrice,
        status = status,
        paymentStatus = paymentStatus,
        note = note,
        qrCodeData = qrCodeData,
        createdAt = createdAt
    )
}

fun BookingResult.toEntity(): TicketEntity {
    return TicketEntity(
        id = id,
        userId = userId,
        userName = userName,
        courtId = courtId,
        courtName = courtName,
        stadiumId = stadiumId,
        stadiumName = stadiumName,
        stadiumAddress = stadiumAddress,
        stadiumImageUrl = stadiumImageUrl,
        bookingDate = bookingDate,
        startTime = startTime,
        endTime = endTime,
        totalPrice = totalPrice,
        status = status,
        paymentStatus = paymentStatus,
        note = note,
        qrCodeData = qrCodeData,
        createdAt = createdAt
    )
}
