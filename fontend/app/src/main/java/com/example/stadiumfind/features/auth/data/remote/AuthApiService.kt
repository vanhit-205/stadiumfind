package com.example.stadiumfind.features.auth.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.features.auth.data.remote.dto.AuthResponseDto
import com.example.stadiumfind.features.auth.data.remote.dto.LoginRequest
import com.example.stadiumfind.features.auth.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<BaseResponse<AuthResponseDto>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BaseResponse<AuthResponseDto>>
}
