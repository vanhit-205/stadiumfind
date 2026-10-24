package com.example.stadiumfind.features.booking.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

/**
 * UseCase thực hiện việc tạo đơn đặt sân.
 * Kiểm tra dữ liệu đầu vào và chuyển đổi định dạng thời gian chuẩn trước khi gọi Repository.
 */
class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(
        courtId: Long,
        bookingDate: String,
        startTime: String,
        endTime: String,
        note: String? = null,
        userId: Long? = null
    ): Resource<BookingResult> {
        if (courtId <= 0L) {
            return Resource.Error("Vui lòng chọn sân con hợp lệ")
        }
        if (bookingDate.isBlank()) {
            return Resource.Error("Vui lòng chọn ngày đặt sân")
        }
        if (startTime.isBlank() || endTime.isBlank()) {
            return Resource.Error("Vui lòng chọn khung giờ đặt sân")
        }

        // Chuẩn hóa định dạng thời gian sang HH:mm:ss nếu cần
        val formattedStart = normalizeTime(startTime)
        val formattedEnd = normalizeTime(endTime)

        return bookingRepository.createBooking(
            courtId = courtId,
            bookingDate = bookingDate,
            startTime = formattedStart,
            endTime = formattedEnd,
            note = note?.trim()?.ifBlank { null },
            userId = userId
        )
    }

    private fun normalizeTime(time: String): String {
        val trimmed = time.trim()
        return when {
            trimmed.matches(Regex("^\\d{1,2}:\\d{2}$")) -> {
                val parts = trimmed.split(":")
                val hour = parts[0].padStart(2, '0')
                val minute = parts[1]
                "$hour:$minute:00"
            }
            trimmed.matches(Regex("^\\d{1,2}:\\d{2}:\\d{2}$")) -> {
                val parts = trimmed.split(":")
                val hour = parts[0].padStart(2, '0')
                "$hour:${parts[1]}:${parts[2]}"
            }
            else -> trimmed
        }
    }
}
