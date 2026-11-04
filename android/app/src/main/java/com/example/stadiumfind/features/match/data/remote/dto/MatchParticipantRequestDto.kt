package com.example.stadiumfind.features.match.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchParticipantRequestDto(
    @SerializedName("matchPostId")
    val matchPostId: Long? = null,
    @SerializedName("userId")
    val userId: Long? = null,
    @SerializedName("note")
    val note: String? = null
)
