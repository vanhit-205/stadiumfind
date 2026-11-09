package com.example.stadiumfind.features.owner.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.features.owner.data.remote.dto.OwnerRevenueResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OwnerStatisticsApiService {

    @GET("owner/statistics/revenue")
    suspend fun getRevenueStatistics(
        @Query("stadiumId") stadiumId: Long? = null,
        @Query("type") type: String = "DAILY",
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null
    ): Response<BaseResponse<OwnerRevenueResponseDto>>
}
