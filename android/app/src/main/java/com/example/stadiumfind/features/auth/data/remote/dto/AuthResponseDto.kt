package com.example.stadiumfind.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("tokenType")
    val tokenType: String?,
    @SerializedName("expiresIn")
    val expiresIn: Long?,
    @SerializedName("user")
    val user: UserDto?
)
