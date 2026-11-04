package com.example.stadiumfind.features.auth.domain.model

data class AuthResult(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long?,
    val user: UserModel?
)
