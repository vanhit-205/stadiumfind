package com.example.stadiumfind.features.notification.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.notification.domain.model.NotificationItem
import com.example.stadiumfind.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(page: Int = 0, size: Int = 20): Flow<Resource<List<NotificationItem>>> {
        return repository.getNotifications(page, size)
    }
}

class GetUnreadNotificationCountUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<Resource<Long>> {
        return repository.getUnreadCount()
    }
}

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(id: Long): Flow<Resource<Unit>> {
        return repository.markAsRead(id)
    }
}

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> {
        return repository.markAllAsRead()
    }
}
