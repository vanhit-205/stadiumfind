package com.example.stadiumfind.features.match.domain.usecase

import com.example.stadiumfind.features.match.domain.model.MatchTypeSpec
import com.example.stadiumfind.features.match.domain.model.PitchSourceSpec
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: Map<String, String>) : ValidationResult() {
        val firstErrorMessage: String
            get() = errors.values.firstOrNull() ?: "Thông tin không hợp lệ"
    }
}

class ValidateMatchPostFormUseCase {

    operator fun invoke(
        title: String,
        matchType: MatchTypeSpec,
        pitchSource: PitchSourceSpec,
        playDate: String,
        startTime: String,
        endTime: String,
        maxParticipants: Int,
        pricePerPerson: Double
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        // 1. Tiêu đề
        if (title.isBlank()) {
            errors["title"] = "Tiêu đề bài đăng không được để trống"
        } else if (title.length < 5) {
            errors["title"] = "Tiêu đề phải có ít nhất 5 ký tự"
        }

        // 2. Thời gian bắt đầu > hiện tại + 60 phút
        try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val cleanStartTime = startTime.take(5)
            val parsedDate = LocalDate.parse(playDate, dateFormatter)
            val parsedStartTime = LocalTime.parse(cleanStartTime, timeFormatter)
            val matchDateTime = LocalDateTime.of(parsedDate, parsedStartTime)

            val minimumAllowedTime = LocalDateTime.now().plusMinutes(60)
            if (matchDateTime.isBefore(minimumAllowedTime)) {
                errors["startTime"] = "Thời gian đá bóng phải cách hiện tại ít nhất 60 phút để kịp ghép đội"
            }
        } catch (e: Exception) {
            // Không parse được theo định dạng chuẩn thì bỏ qua kiểm tra ngày giờ tương đối
        }

        // 3. Số người tham gia
        if (maxParticipants <= 0) {
            errors["maxParticipants"] = "Số lượng người tham gia phải lớn hơn 0"
        }

        // 4. Chi phí
        if (pricePerPerson < 0) {
            errors["price"] = "Chi phí không được âm"
        }

        // 5. Kiểm tra theo nguồn sân
        when (pitchSource) {
            is PitchSourceSpec.VerifiedBooking -> {
                if (pitchSource.bookingId <= 0L) {
                    errors["bookingId"] = "Mã đơn đặt sân không hợp lệ"
                }
            }
            is PitchSourceSpec.ExternalPitch -> {
                if (pitchSource.pitchName.isBlank()) {
                    errors["pitchName"] = "Vui lòng nhập tên sân bóng ngoài hệ thống"
                }
                if (pitchSource.address.isBlank()) {
                    errors["pitchAddress"] = "Vui lòng nhập địa chỉ sân bóng"
                }
            }
            is PitchSourceSpec.NoPitchYet -> {
                if (pitchSource.preferredAreaName.isBlank()) {
                    errors["preferredArea"] = "Vui lòng chọn khu vực bạn muốn tìm sân"
                }
            }
        }

        // 6. Kiểm tra theo loại kèo
        when (matchType) {
            is MatchTypeSpec.SlotRecruitment -> {
                val totalSlots = matchType.requiredPositions.values.sum()
                if (totalSlots == 0 && maxParticipants <= 1) {
                    errors["slots"] = "Vui lòng chọn ít nhất 1 vị trí cần tuyển người"
                }
            }
            else -> Unit
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
