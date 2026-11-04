package com.example.stadiumfind.features.notification.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.notification.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(page: Int = 0, size: Int = 20): Flow<Resource<List<NotificationItem>>>
    fun getUnreadNotifications(page: Int = 0, size: Int = 20): Flow<Resource<List<NotificationItem>>>
    fun getUnreadCount(): Flow<Resource<Long>>
    fun markAsRead(id: Long): Flow<Resource<Unit>>
    fun markAllAsRead(): Flow<Resource<Unit>>
}
