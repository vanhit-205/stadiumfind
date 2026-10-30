package com.example.stadiumfind.features.match.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchParticipantDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("matchPostId")
    val matchPostId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("userAvatar")
    val userAvatar: String?,
    @SerializedName("userPhoneNumber")
    val userPhoneNumber: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("note")
    val note: String?,
    @SerializedName("joinedAt")
    val joinedAt: String?,
    @SerializedName("matchTitle")
    val matchTitle: String? = null,
    @SerializedName("sportType")
    val sportType: String? = null,
    @SerializedName("playDate")
    val playDate: String? = null,
    @SerializedName("startTime")
    val startTime: String? = null,
    @SerializedName("endTime")
    val endTime: String? = null,
    @SerializedName("stadiumName")
    val stadiumName: String? = null,
    @SerializedName("courtName")
    val courtName: String? = null,
    @SerializedName("maxParticipants")
    val maxParticipants: Int? = null,
    @SerializedName("currentParticipants")
    val currentParticipants: Int? = null,
    @SerializedName("pricePerPerson")
    val pricePerPerson: Double? = null,
    @SerializedName("matchStatus")
    val matchStatus: String? = null,
    @SerializedName("creatorName")
    val creatorName: String? = null,
    @SerializedName("creatorAvatar")
    val creatorAvatar: String? = null,
    @SerializedName("creatorPhone")
    val creatorPhone: String? = null
)
