package com.example.stadiumfind.features.booking.domain.model

/**
 * Model biểu diễn sân con (Court) thuộc về một cụm sân (Stadium).
 */
data class CourtItem(
    val id: Long,
    val name: String,           // Ví dụ: "Sân 5 - Số 1", "Sân 7 - VIP"
    val sportType: String,      // "Bóng đá 5 người", "Bóng đá 7 người"
    val pricePerHour: Double? = null,
    val isSelected: Boolean = false
)
