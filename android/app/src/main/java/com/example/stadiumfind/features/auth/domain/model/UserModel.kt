package com.example.stadiumfind.features.auth.domain.model

data class UserModel(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String?,
    val avatarUrl: String?,
    val role: String?
)
