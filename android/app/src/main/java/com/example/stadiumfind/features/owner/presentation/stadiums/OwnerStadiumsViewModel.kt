package com.example.stadiumfind.features.owner.presentation.stadiums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OwnerStadiumsUiState {
    object Loading : OwnerStadiumsUiState()
    data class Success(val stadiums: List<StadiumItem>) : OwnerStadiumsUiState()
    data class Error(val message: String) : OwnerStadiumsUiState()
}

@HiltViewModel
class OwnerStadiumsViewModel @Inject constructor(
    private val stadiumRepository: StadiumRepository,
    private val userPreferences: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<OwnerStadiumsUiState>(OwnerStadiumsUiState.Loading)
    val uiState: StateFlow<OwnerStadiumsUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadOwnerStadiums()
    }

    fun loadOwnerStadiums() {
        viewModelScope.launch {
            _uiState.value = OwnerStadiumsUiState.Loading

            // Gọi API getMyStadiums() trực tiếp từ Access Token của Owner đăng nhập
            when (val result = stadiumRepository.getMyStadiums()) {
                is Resource.Success -> {
                    _uiState.value = OwnerStadiumsUiState.Success(result.data)
                }
                is Resource.Error -> {
                    // Fallback theo ownerId từ DataStore nếu có
                    val ownerId = userPreferences.userId.firstOrNull()
                    if (ownerId != null) {
                        when (val fallbackResult = stadiumRepository.getStadiumsByOwner(ownerId)) {
                            is Resource.Success -> {
                                _uiState.value = OwnerStadiumsUiState.Success(fallbackResult.data)
                                return@launch
                            }
                            is Resource.Error -> {
                                _uiState.value = OwnerStadiumsUiState.Error(fallbackResult.message)
                                return@launch
                            }
                            is Resource.Loading -> Unit
                        }
                    }
                    _uiState.value = OwnerStadiumsUiState.Error(result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun toggleStadiumStatus(stadium: StadiumItem, newStatus: Boolean) {
        viewModelScope.launch {
            when (val result = stadiumRepository.updateStadiumStatus(stadium.id, newStatus)) {
                is Resource.Success -> {
                    val statusText = if (newStatus) "mở cửa" else "tạm đóng cửa"
                    _toastEvent.emit("Đã đổi trạng thái ${stadium.name} sang $statusText")
                    // Cập nhật lại UI state cục bộ
                    val currentState = _uiState.value
                    if (currentState is OwnerStadiumsUiState.Success) {
                        val updatedList = currentState.stadiums.map {
                            if (it.id == stadium.id) it.copy(isActive = newStatus) else it
                        }
                        _uiState.value = OwnerStadiumsUiState.Success(updatedList)
                    }
                }
                is Resource.Error -> {
                    _toastEvent.emit("Lỗi: ${result.message}")
                    // Tải lại dữ liệu để đồng bộ đúng trạng thái từ server
                    loadOwnerStadiums()
                }
                is Resource.Loading -> Unit
            }
        }
    }
}
