package com.example.stadiumfind.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("avatarUrl")
    val avatarUrl: String?,
    @SerializedName("isActive")
    val isActive: Boolean?
)
