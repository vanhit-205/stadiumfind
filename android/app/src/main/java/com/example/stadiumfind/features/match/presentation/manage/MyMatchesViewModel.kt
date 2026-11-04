package com.example.stadiumfind.features.match.presentation.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.match.domain.model.MatchParticipant
import com.example.stadiumfind.features.match.domain.usecase.GetMyMatchesUseCase
import com.example.stadiumfind.features.match.domain.usecase.LeaveMatchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyMatchesUiState(
    val isLoading: Boolean = false,
    val selectedStatus: String? = null, // null = ALL, "PENDING", "ACCEPTED", "REJECTED"
    val matches: List<MatchParticipant> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class MyMatchesViewModel @Inject constructor(
    private val getMyMatchesUseCase: GetMyMatchesUseCase,
    private val leaveMatchUseCase: LeaveMatchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyMatchesUiState())
    val uiState: StateFlow<MyMatchesUiState> = _uiState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadMyMatches(null)
    }

    fun filterByStatus(status: String?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        loadMyMatches(status)
    }

    fun loadMyMatches(status: String? = _uiState.value.selectedStatus) {
        viewModelScope.launch {
            getMyMatchesUseCase(status = status).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            matches = result.data,
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

    fun leaveMatch(matchPostId: Long) {
        viewModelScope.launch {
            leaveMatchUseCase(matchPostId).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _toastEvent.emit("Đã huỷ tham gia kèo thành công")
                        loadMyMatches()
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _toastEvent.emit(result.message ?: "Không thể rút lui khỏi kèo")
                    }
                }
            }
        }
    }
}
