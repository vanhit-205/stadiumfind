package com.example.stadiumfind.features.booking.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot

interface BookingRepository {

    /**
     * Lấy danh sách khung giờ đã được đặt trước trong một ngày của một sân con.
     */
    suspend fun getOccupiedSlots(courtId: Long, date: String): Resource<List<OccupiedSlot>>

    /**
     * Lấy danh sách các sân con thuộc cụm sân.
     */
    suspend fun getCourtsByStadium(stadiumId: Long): Resource<List<CourtItem>>

    /**
     * Tạo đơn đặt sân mới (Ngày 22 - Booking Flow).
     */
    suspend fun createBooking(
        courtId: Long,
        bookingDate: String,
        startTime: String,
        endTime: String,
        note: String? = null,
        userId: Long? = null
    ): Resource<BookingResult>

    /**
     * Lấy thông tin vé đặt theo ID (Ngày 22 & Ngày 23).
     */
    suspend fun getBookingById(id: Long): Resource<BookingResult>

    /**
     * Lấy toàn bộ danh sách vé đặt sân của người dùng đang đăng nhập (Ngày 24 - My Bookings).
     */
    suspend fun getMyBookings(): Resource<List<BookingResult>>
}
