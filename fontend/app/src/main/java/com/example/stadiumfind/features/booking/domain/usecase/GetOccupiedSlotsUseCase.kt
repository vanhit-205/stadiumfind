package com.example.stadiumfind.features.booking.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * UseCase lấy danh sách khung giờ đã có người đặt trước theo sân con và ngày cụ thể.
 */
class GetOccupiedSlotsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(courtId: Long, date: String): Resource<List<OccupiedSlot>> {
        return repository.getOccupiedSlots(courtId, date)
    }
}
