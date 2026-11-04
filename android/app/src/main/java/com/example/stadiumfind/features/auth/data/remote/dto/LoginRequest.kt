package com.example.stadiumfind.features.auth.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Login request – backend nhận field "usernameOrEmail" (có thể là email hoặc username).
 */
data class LoginRequest(
    @SerializedName("usernameOrEmail")
    val usernameOrEmail: String,
    @SerializedName("password")
    val password: String
)
