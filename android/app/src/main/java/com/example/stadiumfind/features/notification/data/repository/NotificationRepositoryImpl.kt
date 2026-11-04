package com.example.stadiumfind.features.notification.data.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.notification.data.remote.NotificationApiService
import com.example.stadiumfind.features.notification.data.remote.dto.NotificationDto
import com.example.stadiumfind.features.notification.domain.model.NotificationItem
import com.example.stadiumfind.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val apiService: NotificationApiService
) : NotificationRepository {

    override fun getNotifications(page: Int, size: Int): Flow<Resource<List<NotificationItem>>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { apiService.getNotifications(page, size) }
        when (result) {
            is Resource.Success -> {
                val list = result.data.content.map { it.toDomain() }
                emit(Resource.Success(list))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun getUnreadNotifications(page: Int, size: Int): Flow<Resource<List<NotificationItem>>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall { apiService.getUnreadNotifications(page, size) }
        when (result) {
            is Resource.Success -> {
                val list = result.data.content.map { it.toDomain() }
                emit(Resource.Success(list))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun getUnreadCount(): Flow<Resource<Long>> = flow {
        val result = safeApiCall { apiService.getUnreadCount() }
        when (result) {
            is Resource.Success -> emit(Resource.Success(result.data))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun markAsRead(id: Long): Flow<Resource<Unit>> = flow {
        val result = safeApiCall { apiService.markAsRead(id) }
        when (result) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun markAllAsRead(): Flow<Resource<Unit>> = flow {
        val result = safeApiCall { apiService.markAllAsRead() }
        when (result) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    private fun NotificationDto.toDomain(): NotificationItem {
        return NotificationItem(
            id = id,
            userId = userId,
            title = title,
            content = content,
            type = type ?: "SYSTEM",
            isRead = isRead ?: false,
            referenceId = referenceId,
            createdAt = createdAt
        )
    }
}
