package com.example.stadiumfind.features.match.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchPostDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("creatorId")
    val creatorId: Long,
    @SerializedName("creatorName")
    val creatorName: String?,
    @SerializedName("creatorAvatar")
    val creatorAvatar: String?,
    @SerializedName("courtId")
    val courtId: Long?,
    @SerializedName("courtName")
    val courtName: String?,
    @SerializedName("stadiumId")
    val stadiumId: Long?,
    @SerializedName("stadiumName")
    val stadiumName: String?,
    @SerializedName("bookingId")
    val bookingId: Long?,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("sportType")
    val sportType: String,
    @SerializedName("playDate")
    val playDate: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("maxParticipants")
    val maxParticipants: Int?,
    @SerializedName("currentParticipants")
    val currentParticipants: Int?,
    @SerializedName("pricePerPerson")
    val pricePerPerson: Double?,
    @SerializedName("status")
    val status: String,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)
