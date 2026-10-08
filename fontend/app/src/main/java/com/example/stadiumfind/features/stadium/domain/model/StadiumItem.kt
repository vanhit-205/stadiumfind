package com.example.stadiumfind.features.stadium.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Model biểu diễn thông tin hiển thị của một sân bóng trên danh sách CardView Home.
 */
data class StadiumItem(
    val id: Long,
    val name: String,
    val address: String,
    val provinceCity: String? = null,
    val district: String? = null,
    val phoneNumber: String? = null,
    val description: String? = null,
    val openingTime: String? = null,
    val closingTime: String? = null,
    val imageUrl: String? = null,
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val minPricePerHour: Double = 0.0,
    val totalCourts: Int = 1,
    val sportTypes: List<String> = listOf("Bóng đá")
) {
    val formattedPrice: String
        get() {
            if (minPricePerHour <= 0) return "Liên hệ"
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
            }
            val formatter = DecimalFormat("#,###", symbols)
            return "${formatter.format(minPricePerHour)}đ / giờ"
        }

    val formattedOperatingHours: String
        get() {
            return if (!openingTime.isNullOrBlank() && !closingTime.isNullOrBlank()) {
                "${openingTime.take(5)} - ${closingTime.take(5)}"
            } else {
                "06:00 - 22:30"
            }
        }

    val primarySportBadge: String
        get() {
            return if (sportTypes.isNotEmpty()) {
                sportTypes.first()
            } else {
                "Bóng đá"
            }
        }

    val locationShort: String
        get() {
            val parts = mutableListOf<String>()
            if (!district.isNullOrBlank()) parts.add(district)
            if (!provinceCity.isNullOrBlank()) parts.add(provinceCity)
            return if (parts.isNotEmpty()) parts.joinToString(", ") else address
        }
}
