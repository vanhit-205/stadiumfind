package com.example.stadiumfind.features.stadium.domain.repository

import androidx.paging.PagingData
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface Repository quản lý dữ liệu sân bóng theo kiến trúc Clean Architecture.
 */
interface StadiumRepository {

    /**
     * Lấy luồng phân trang PagingData cho danh sách sân bóng.
     */
    fun getStadiumsStream(
        keyword: String? = null,
        provinceCity: String? = null
    ): Flow<PagingData<StadiumItem>>

    /**
     * Lấy danh sách sân bóng gần vị trí hiện tại theo bán kính (km).
     */
    suspend fun getNearbyStadiums(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 20.0
    ): com.example.stadiumfind.core.network.Resource<List<StadiumItem>>
}
