package com.example.stadiumfind.features.booking.domain.model

/**
 * Định nghĩa thời lượng ca đá bóng:
 * - DURATION_60_MIN: Ca 60 phút (1 giờ), hệ số giá 1.0
 * - DURATION_90_MIN: Ca 90 phút (1.5 giờ - ca bóng đá tiêu chuẩn), hệ số giá 1.5
 */
enum class SlotDuration(
    val minutes: Int,
    val durationHours: Double,
    val displayName: String,
    val shortLabel: String
) {
    DURATION_60_MIN(
        minutes = 60,
        durationHours = 1.0,
        displayName = "Ca 60 phút",
        shortLabel = "60p"
    ),
    DURATION_90_MIN(
        minutes = 90,
        durationHours = 1.5,
        displayName = "Ca 90 phút (Tiêu chuẩn)",
        shortLabel = "90p"
    );

    val durationMinutes: Int
        get() = minutes
}
