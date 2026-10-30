package com.example.stadiumfind.features.notification.domain.model

data class NotificationItem(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val type: String,
    val isRead: Boolean,
    val referenceId: Long?,
    val createdAt: String?
) {
    val isMatchNotification: Boolean
        get() = type.equals("MATCH", ignoreCase = true) || type.equals("MATCH_JOIN_REQUEST", ignoreCase = true)

    val isBookingNotification: Boolean
        get() = type.equals("BOOKING", ignoreCase = true)
}
