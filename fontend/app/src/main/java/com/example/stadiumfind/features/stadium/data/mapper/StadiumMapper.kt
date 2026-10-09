package com.example.stadiumfind.features.stadium.data.mapper

import com.example.stadiumfind.features.stadium.data.remote.dto.StadiumDto
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem

/**
 * Extension function chuyển đổi dữ liệu từ StadiumDto sang StadiumItem Domain model.
 */
fun StadiumDto.toDomain(): StadiumItem {
    return StadiumItem(
        id = id,
        name = name,
        address = address,
        provinceCity = provinceCity,
        district = district,
        phoneNumber = phoneNumber,
        description = description,
        openingTime = openingTime,
        closingTime = closingTime,
        imageUrl = imageUrl,
        rating = rating ?: 5.0,
        reviewCount = 0,
        minPricePerHour = minPricePerHour ?: 0.0,
        totalCourts = totalCourts ?: 1,
        sportTypes = if (!sportTypes.isNullOrEmpty()) sportTypes else listOf("Bóng đá")
    )
}
