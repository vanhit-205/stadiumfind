package com.example.stadiumfind.features.profile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FcmTokenRequestDto(
    @SerializedName("fcmToken")
    val fcmToken: String
)
