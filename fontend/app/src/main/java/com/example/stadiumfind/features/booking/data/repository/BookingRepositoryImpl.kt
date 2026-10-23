package com.example.stadiumfind.features.booking.data.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.booking.data.remote.BookingApiService
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService
) : BookingRepository {

    override suspend fun getOccupiedSlots(courtId: Long, date: String): Resource<List<OccupiedSlot>> {
        val result = safeApiCall {
            apiService.getOccupiedSlots(courtId, date)
        }
        return when (result) {
            is Resource.Success -> {
                val items = result.data.map { it.toDomain() }
                Resource.Success(items)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getCourtsByStadium(stadiumId: Long): Resource<List<CourtItem>> {
        val result = safeApiCall {
            apiService.getCourtsByStadium(stadiumId)
        }
        return when (result) {
            is Resource.Success -> {
                val items = result.data.mapIndexed { index, dto ->
                    dto.toDomain(isSelected = (index == 0))
                }
                Resource.Success(items)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
