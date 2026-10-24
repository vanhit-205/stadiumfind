package com.example.stadiumfind.features.booking.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.features.booking.data.remote.dto.CourtDto
import com.example.stadiumfind.features.booking.data.remote.dto.OccupiedSlotDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookingApiService {

    /**
     * Lấy danh sách các khung giờ đã có người đặt trước của một sân con trong ngày.
     */
    @GET("bookings/occupied-slots")
    suspend fun getOccupiedSlots(
        @Query("courtId") courtId: Long,
        @Query("date") date: String
    ): Response<BaseResponse<List<OccupiedSlotDto>>>

    /**
     * Lấy danh sách sân con thuộc cụm sân (Stadium).
     */
    @GET("courts")
    suspend fun getCourtsByStadium(
        @Query("stadiumId") stadiumId: Long
    ): Response<BaseResponse<List<CourtDto>>>

    /**
     * Tạo đơn đặt sân mới (Ngày 22 - Booking Flow).
     */
    @POST("bookings")
    suspend fun createBooking(
        @Body request: com.example.stadiumfind.features.booking.data.remote.dto.BookingCreateRequestDto
    ): Response<BaseResponse<com.example.stadiumfind.features.booking.data.remote.dto.BookingResponseDto>>

    /**
     * Lấy chi tiết đơn đặt sân theo ID (Ngày 22 & Ngày 23).
     */
    @GET("bookings/{id}")
    suspend fun getBookingById(
        @Path("id") id: Long
    ): Response<BaseResponse<com.example.stadiumfind.features.booking.data.remote.dto.BookingResponseDto>>
}
