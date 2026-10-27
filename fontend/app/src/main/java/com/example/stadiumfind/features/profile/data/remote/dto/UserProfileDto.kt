package com.example.stadiumfind.features.profile.data.remote.dto

import com.example.stadiumfind.features.profile.domain.model.UserProfile
import com.google.gson.annotations.SerializedName

data class UserProfileDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("totalBookings")
    val totalBookings: Long? = 0L,

    @SerializedName("createdAt")
    val createdAt: String? = null
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            username = username ?: "",
            email = email ?: "",
            fullName = fullName ?: username ?: "Người dùng",
            phoneNumber = phoneNumber ?: "",
            role = role ?: "USER",
            avatarUrl = avatarUrl,
            totalBookings = totalBookings ?: 0L,
            createdAt = createdAt
        )
    }
}

data class UserUpdateRequestDto(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null
)
