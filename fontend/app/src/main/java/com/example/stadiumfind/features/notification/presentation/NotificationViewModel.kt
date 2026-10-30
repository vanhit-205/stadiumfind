package com.example.stadiumfind.features.notification.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.notification.domain.model.NotificationItem
import com.example.stadiumfind.features.notification.domain.usecase.GetNotificationsUseCase
import com.example.stadiumfind.features.notification.domain.usecase.GetUnreadNotificationCountUseCase
import com.example.stadiumfind.features.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.stadiumfind.features.notification.domain.usecase.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationItem> = emptyList(),
    val unreadCount: Long = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            notifications = result.data,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            getUnreadNotificationCountUseCase().collect { result ->
                if (result is Resource.Success) {
                    _uiState.value = _uiState.value.copy(unreadCount = result.data)
                }
            }
        }
    }

    fun markAsRead(notification: NotificationItem) {
        if (notification.isRead) return
        viewModelScope.launch {
            markNotificationAsReadUseCase(notification.id).collect { result ->
                if (result is Resource.Success) {
                    val updatedList = _uiState.value.notifications.map {
                        if (it.id == notification.id) it.copy(isRead = true) else it
                    }
                    val newCount = maxOf(0L, _uiState.value.unreadCount - 1)
                    _uiState.value = _uiState.value.copy(
                        notifications = updatedList,
                        unreadCount = newCount
                    )
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            markAllNotificationsAsReadUseCase().collect { result ->
                if (result is Resource.Success) {
                    val updatedList = _uiState.value.notifications.map { it.copy(isRead = true) }
                    _uiState.value = _uiState.value.copy(
                        notifications = updatedList,
                        unreadCount = 0
                    )
                }
            }
        }
    }
}
