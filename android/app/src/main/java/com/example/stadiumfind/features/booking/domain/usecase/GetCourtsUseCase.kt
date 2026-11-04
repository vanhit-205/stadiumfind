package com.example.stadiumfind.features.booking.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * UseCase lấy danh sách sân con thuộc về một cụm sân (Stadium).
 */
class GetCourtsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(stadiumId: Long): Resource<List<CourtItem>> {
        return repository.getCourtsByStadium(stadiumId)
    }
}
