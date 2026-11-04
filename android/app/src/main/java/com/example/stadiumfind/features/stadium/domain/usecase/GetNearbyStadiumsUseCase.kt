package com.example.stadiumfind.features.stadium.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import javax.inject.Inject

/**
 * UseCase lấy danh sách sân bóng gần vị trí hiện tại theo bán kính (km).
 */
class GetNearbyStadiumsUseCase @Inject constructor(
    private val repository: StadiumRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): Resource<List<StadiumItem>> {
        return repository.getNearbyStadiums(
            latitude = latitude,
            longitude = longitude,
            radiusKm = radiusKm
        )
    }
}
