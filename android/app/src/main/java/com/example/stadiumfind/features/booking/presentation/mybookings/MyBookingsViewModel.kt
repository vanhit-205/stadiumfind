package com.example.stadiumfind.features.booking.presentation.mybookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.usecase.GetMyBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MyBookingsUiState {
    object Idle : MyBookingsUiState()
    object Loading : MyBookingsUiState()
    data class Success(
        val upcoming: List<BookingResult>,
        val history: List<BookingResult>
    ) : MyBookingsUiState()
    data class Error(val message: String) : MyBookingsUiState()
}

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val getMyBookingsUseCase: GetMyBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyBookingsUiState>(MyBookingsUiState.Idle)
    val uiState: StateFlow<MyBookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.value = MyBookingsUiState.Loading
            when (val result = getMyBookingsUseCase()) {
                is Resource.Success -> {
                    _uiState.value = MyBookingsUiState.Success(
                        upcoming = result.data.upcoming,
                        history = result.data.history
                    )
                }
                is Resource.Error -> {
                    _uiState.value = MyBookingsUiState.Error(result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = MyBookingsUiState.Loading
                }
            }
        }
    }
}
