package com.example.stadiumfind.features.booking.domain.repository

import com.example.stadiumfind.core.network.Resource
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
}
