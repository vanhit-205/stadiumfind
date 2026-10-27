package com.example.stadiumfind.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.profile.domain.model.UserProfile
import com.example.stadiumfind.features.profile.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Idle : ProfileUiState()
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userPreferences: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            // Trước tiên thử đọc thông tin cached từ DataStore nếu có
            val cachedName = userPreferences.username.firstOrNull()
            val cachedId = userPreferences.userId.firstOrNull()

            when (val result = getUserProfileUseCase()) {
                is Resource.Success -> {
                    _uiState.value = ProfileUiState.Success(result.data)
                }
                is Resource.Error -> {
                    // Nếu lỗi mạng nhưng có cached, vẫn có thể fallback hiển thị
                    if (cachedId != null && !cachedName.isNullOrEmpty()) {
                        _uiState.value = ProfileUiState.Success(
                            UserProfile(
                                id = cachedId,
                                username = cachedName,
                                email = "user@stadiumfind.com",
                                fullName = cachedName,
                                phoneNumber = "",
                                role = "USER",
                                avatarUrl = null,
                                totalBookings = 0L,
                                createdAt = null
                            )
                        )
                    } else {
                        _uiState.value = ProfileUiState.Error(result.message)
                    }
                }
                is Resource.Loading -> {
                    _uiState.value = ProfileUiState.Loading
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearAll()
            onComplete()
        }
    }
}
