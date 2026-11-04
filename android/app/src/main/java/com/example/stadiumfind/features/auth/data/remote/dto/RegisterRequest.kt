package com.example.stadiumfind.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Backend (UserRegisterRequest) bắt buộc thêm fullName; role do server tự gán (USER).
 * Form đăng ký hiện chỉ có username/email/password nên mặc định fullName = username.
 */
data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("fullName")
    val fullName: String = username
)
