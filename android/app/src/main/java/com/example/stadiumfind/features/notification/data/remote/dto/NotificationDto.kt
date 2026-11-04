package com.example.stadiumfind.features.notification.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String?,
    @SerializedName("isRead")
    val isRead: Boolean?,
    @SerializedName("referenceId")
    val referenceId: Long?,
    @SerializedName("createdAt")
    val createdAt: String?
)
