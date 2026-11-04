package com.example.stadiumfind.features.profile.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.features.profile.data.remote.dto.UserProfileDto
import com.example.stadiumfind.features.profile.data.remote.dto.UserUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApiService {

    @GET("users/me")
    suspend fun getProfile(): Response<BaseResponse<UserProfileDto>>

    @PUT("users/me")
    suspend fun updateProfile(
        @Body request: UserUpdateRequestDto
    ): Response<BaseResponse<UserProfileDto>>
}
