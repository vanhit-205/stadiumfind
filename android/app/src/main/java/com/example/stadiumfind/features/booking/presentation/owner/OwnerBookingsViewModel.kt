package com.example.stadiumfind.features.booking.presentation.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.BookingResult
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OwnerBookingsUiState {
    object Idle : OwnerBookingsUiState()
    object Loading : OwnerBookingsUiState()
    data class Success(val bookings: List<BookingResult>) : OwnerBookingsUiState()
    data class Error(val message: String) : OwnerBookingsUiState()
}

@HiltViewModel
class OwnerBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OwnerBookingsUiState>(OwnerBookingsUiState.Idle)
    val uiState: StateFlow<OwnerBookingsUiState> = _uiState.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        loadOwnerBookings()
    }

    fun loadOwnerBookings() {
        viewModelScope.launch {
            _uiState.value = OwnerBookingsUiState.Loading
            when (val result = bookingRepository.getOwnerBookings()) {
                is Resource.Success -> {
                    _uiState.value = OwnerBookingsUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = OwnerBookingsUiState.Error(result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = OwnerBookingsUiState.Loading
                }
            }
        }
    }

    fun approveBooking(bookingId: Long) {
        viewModelScope.launch {
            when (val result = bookingRepository.updateBookingStatus(bookingId, "CONFIRMED")) {
                is Resource.Success -> {
                    _actionMessage.value = "Đã duyệt đơn #${bookingId} thành công!"
                    loadOwnerBookings()
                }
                is Resource.Error -> {
                    _actionMessage.value = "Lỗi khi duyệt đơn: ${result.message}"
                }
                else -> {}
            }
        }
    }

    fun rejectBooking(bookingId: Long) {
        viewModelScope.launch {
            when (val result = bookingRepository.updateBookingStatus(bookingId, "CANCELLED")) {
                is Resource.Success -> {
                    _actionMessage.value = "Đã từ chối đơn #${bookingId}"
                    loadOwnerBookings()
                }
                is Resource.Error -> {
                    _actionMessage.value = "Lỗi khi từ chối đơn: ${result.message}"
                }
                else -> {}
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
