package com.example.stadiumfind.features.match.domain.model

data class MatchParticipant(
    val id: Long,
    val matchPostId: Long,
    val userId: Long,
    val userName: String,
    val userAvatar: String?,
    val userPhoneNumber: String?,
    val status: String,
    val note: String?,
    val joinedAt: String?,
    val matchTitle: String? = null,
    val sportType: String? = null,
    val playDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val stadiumName: String? = null,
    val courtName: String? = null,
    val maxParticipants: Int = 10,
    val currentParticipants: Int = 1,
    val pricePerPerson: Double = 0.0,
    val matchStatus: String? = null,
    val creatorName: String? = null,
    val creatorAvatar: String? = null,
    val creatorPhone: String? = null
) {
    val isAccepted: Boolean
        get() = status.equals("ACCEPTED", ignoreCase = true)

    val isPending: Boolean
        get() = status.equals("PENDING", ignoreCase = true)

    val isRejected: Boolean
        get() = status.equals("REJECTED", ignoreCase = true)

    val isCancelled: Boolean
        get() = status.equals("CANCELLED", ignoreCase = true)
}
