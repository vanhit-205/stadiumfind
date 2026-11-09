package com.example.stadiumfind.features.owner.presentation.revenue

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.owner.domain.model.OwnerRevenue
import com.example.stadiumfind.features.owner.domain.repository.OwnerStatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

sealed class OwnerRevenueUiState {
    object Loading : OwnerRevenueUiState()
    data class Success(val revenue: OwnerRevenue) : OwnerRevenueUiState()
    data class Error(val message: String) : OwnerRevenueUiState()
}

@HiltViewModel
class OwnerRevenueViewModel @Inject constructor(
    private val repository: OwnerStatisticsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val stadiumId: Long? = savedStateHandle.get<Long>("stadiumId")?.takeIf { it > 0 }
    val stadiumName: String? = savedStateHandle.get<String>("stadiumName")

    private val cal = Calendar.getInstance()
    var selectedYear: Int = cal.get(Calendar.YEAR)
        private set
    var selectedMonth: Int = cal.get(Calendar.MONTH) + 1
        private set
    var selectedType: String = "DAILY" // "DAILY" hoặc "MONTHLY"
        private set

    private val _uiState = MutableStateFlow<OwnerRevenueUiState>(OwnerRevenueUiState.Loading)
    val uiState: StateFlow<OwnerRevenueUiState> = _uiState.asStateFlow()

    init {
        loadRevenue()
    }

    fun setPeriodType(isMonthly: Boolean) {
        selectedType = if (isMonthly) "MONTHLY" else "DAILY"
        loadRevenue()
    }

    fun setDateFilter(year: Int, month: Int) {
        selectedYear = year
        selectedMonth = month
        loadRevenue()
    }

    fun loadRevenue() {
        viewModelScope.launch {
            _uiState.value = OwnerRevenueUiState.Loading
            val result = repository.getRevenueStatistics(
                stadiumId = stadiumId,
                type = selectedType,
                year = selectedYear,
                month = selectedMonth
            )
            when (result) {
                is Resource.Success -> _uiState.value = OwnerRevenueUiState.Success(result.data)
                is Resource.Error -> _uiState.value = OwnerRevenueUiState.Error(result.message)
                is Resource.Loading -> Unit
            }
        }
    }
}
