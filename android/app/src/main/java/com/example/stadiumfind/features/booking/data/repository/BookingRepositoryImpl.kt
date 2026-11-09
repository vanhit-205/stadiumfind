package com.example.stadiumfind.features.booking.data.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.booking.data.local.dao.TicketDao
import com.example.stadiumfind.features.booking.data.local.mapper.toDomain
import com.example.stadiumfind.features.booking.data.local.mapper.toEntity
import com.example.stadiumfind.features.booking.data.remote.BookingApiService
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.booking.domain.model.OccupiedSlot
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService,
    private val ticketDao: TicketDao
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

    override suspend fun createBooking(
        courtId: Long,
        bookingDate: String,
        startTime: String,
        endTime: String,
        note: String?,
        userId: Long?
    ): Resource<BookingResult> {
        val request = com.example.stadiumfind.features.booking.data.remote.dto.BookingCreateRequestDto(
            courtId = courtId,
            bookingDate = bookingDate,
            startTime = startTime,
            endTime = endTime,
            note = note,
            userId = userId
        )
        val result = safeApiCall {
            apiService.createBooking(request)
        }
        return when (result) {
            is Resource.Success -> {
                val domain = result.data.toDomain()
                try {
                    // Tự động lưu vé mới vào Room Database để có thể xem offline ngay lập tức
                    ticketDao.upsertTicket(domain.toEntity())
                } catch (_: Exception) {
                }
                Resource.Success(domain)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getBookingById(id: Long): Resource<BookingResult> {
        val result = safeApiCall {
            apiService.getBookingById(id)
        }
        return when (result) {
            is Resource.Success -> {
                val domain = result.data.toDomain()
                try {
                    ticketDao.upsertTicket(domain.toEntity())
                } catch (_: Exception) {
                }
                Resource.Success(domain)
            }
            is Resource.Error -> {
                // Fallback đọc vé từ Room Database khi mất mạng
                try {
                    val cached = ticketDao.getTicketById(id)
                    if (cached != null) {
                        Resource.Success(cached.toDomain())
                    } else {
                        Resource.Error(result.message, result.code)
                    }
                } catch (e: Exception) {
                    Resource.Error(result.message, result.code)
                }
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getMyBookings(): Resource<List<BookingResult>> {
        val result = safeApiCall {
            apiService.getMyBookings()
        }
        return when (result) {
            is Resource.Success -> {
                val items = result.data.map { it.toDomain() }
                try {
                    // Đồng bộ vé mới nhất từ server vào Room Database
                    ticketDao.upsertTickets(items.map { it.toEntity() })
                } catch (_: Exception) {
                }
                Resource.Success(items)
            }
            is Resource.Error -> {
                // Offline Fallback: Khi mất mạng hoặc server lỗi, đọc vé đã lưu trong Room
                try {
                    val localTickets = ticketDao.getAllTickets()
                    if (localTickets.isNotEmpty()) {
                        Resource.Success(localTickets.map { it.toDomain() })
                    } else {
                        Resource.Error(result.message, result.code)
                    }
                } catch (e: Exception) {
                    Resource.Error(result.message, result.code)
                }
            }
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun clearCachedBookings() {
        try {
            ticketDao.clearAll()
        } catch (_: Exception) {
        }
    }
}
