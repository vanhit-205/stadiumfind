package com.example.stadiumfind.features.booking.data.remote.dto

import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.google.gson.annotations.SerializedName

/**
 * DTO nhận dữ liệu sân con từ backend:
 * {
 *   "id": 1,
 *   "name": "Sân 5 - Số 1",
 *   "sportType": "Bóng đá 5 người",
 *   "pricePerHour": 250000.0,
 *   "status": "AVAILABLE"
 * }
 */
data class CourtDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("sportType")
    val sportType: String? = null,
    @SerializedName("pricePerHour")
    val pricePerHour: Double? = null,
    @SerializedName("status")
    val status: String? = null
) {
    fun toDomain(isSelected: Boolean = false): CourtItem {
        return CourtItem(
            id = id,
            name = name,
            sportType = sportType ?: "Bóng đá",
            pricePerHour = pricePerHour,
            isSelected = isSelected
        )
    }
}
