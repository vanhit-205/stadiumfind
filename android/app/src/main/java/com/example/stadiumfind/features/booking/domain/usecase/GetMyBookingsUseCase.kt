package com.example.stadiumfind.features.booking.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class GroupedBookings(
    val upcoming: List<BookingResult>,
    val history: List<BookingResult>
)

/**
 * UseCase phân loại danh sách vé đặt sân thành 2 nhóm:
 * - Upcoming (Sắp tới): ngày đặt >= hôm nay VÀ trạng thái khác CANCELLED
 * - History (Lịch sử/Đã đá): ngày đặt < hôm nay HOẶC trạng thái là COMPLETED/CANCELLED
 */
class GetMyBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(): Resource<GroupedBookings> {
        return when (val result = bookingRepository.getMyBookings()) {
            is Resource.Success -> {
                val today = LocalDate.now()
                val upcoming = mutableListOf<BookingResult>()
                val history = mutableListOf<BookingResult>()

                for (booking in result.data) {
                    val isPast = try {
                        val date = LocalDate.parse(booking.bookingDate, DateTimeFormatter.ISO_LOCAL_DATE)
                        date.isBefore(today)
                    } catch (e: Exception) {
                        false
                    }

                    val isFinished = booking.status.equals("COMPLETED", ignoreCase = true) ||
                                     booking.status.equals("CANCELLED", ignoreCase = true)

                    if (isPast || isFinished) {
                        history.add(booking)
                    } else {
                        upcoming.add(booking)
                    }
                }

                Resource.Success(
                    GroupedBookings(
                        upcoming = upcoming,
                        history = history
                    )
                )
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
