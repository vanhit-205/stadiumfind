package com.example.stadiumfind.features.stadium.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.core.network.PageResponse
import com.example.stadiumfind.features.stadium.data.remote.dto.StadiumDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StadiumApiService {

    @GET("stadiums")
    suspend fun getStadiums(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("keyword") keyword: String? = null,
        @Query("provinceCity") provinceCity: String? = null
    ): Response<BaseResponse<PageResponse<StadiumDto>>>

    @GET("stadiums/nearby")
    suspend fun getNearbyStadiums(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 20.0
    ): Response<BaseResponse<List<StadiumDto>>>

    @GET("stadiums/{id}")
    suspend fun getStadiumById(
        @Path("id") id: Long
    ): Response<BaseResponse<StadiumDto>>
}
