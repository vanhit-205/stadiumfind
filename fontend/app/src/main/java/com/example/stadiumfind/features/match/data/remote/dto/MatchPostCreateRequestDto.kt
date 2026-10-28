package com.example.stadiumfind.features.match.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchPostCreateRequestDto(
    @SerializedName("creatorId")
    val creatorId: Long? = null,
    @SerializedName("courtId")
    val courtId: Long,
    @SerializedName("bookingId")
    val bookingId: Long? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("sportType")
    val sportType: String = "FOOTBALL",
    @SerializedName("playDate")
    val playDate: String,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("endTime")
    val endTime: String,
    @SerializedName("maxParticipants")
    val maxParticipants: Int = 10,
    @SerializedName("pricePerPerson")
    val pricePerPerson: Double = 0.0
)
