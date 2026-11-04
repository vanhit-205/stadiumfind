package com.example.stadiumfind.features.match.domain.model

data class MatchPost(
    val id: Long,
    val creatorId: Long,
    val creatorName: String,
    val creatorAvatar: String?,
    val courtId: Long?,
    val courtName: String,
    val stadiumId: Long?,
    val stadiumName: String,
    val bookingId: Long?,
    val title: String,
    val description: String,
    val sportType: String,
    val playDate: String,
    val startTime: String,
    val endTime: String,
    val maxParticipants: Int,
    val currentParticipants: Int,
    val pricePerPerson: Double,
    val status: String
) {
    val isFull: Boolean
        get() = currentParticipants >= maxParticipants || status.equals("FULL", ignoreCase = true)

    val remainingSlots: Int
        get() = (maxParticipants - currentParticipants).coerceAtLeast(0)

    val timeRangeFormatted: String
        get() {
            val start = startTime.take(5)
            val end = endTime.take(5)
            return "$start - $end"
        }
}
