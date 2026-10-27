package com.example.stadiumfind.features.profile.domain.model

data class UserProfile(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val avatarUrl: String?,
    val totalBookings: Long,
    val createdAt: String?
)
