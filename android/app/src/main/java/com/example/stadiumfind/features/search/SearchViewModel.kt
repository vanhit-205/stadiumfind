package com.example.stadiumfind.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.usecase.GetNearbyStadiumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val stadiums: List<StadiumItem>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getNearbyStadiumsUseCase: GetNearbyStadiumsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    /**
     * Tải danh sách sân bóng lân cận theo tọa độ GPS của người dùng.
     */
    fun loadNearbyStadiums(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 25.0,
        forceRefresh: Boolean = false
    ) {
        if (!forceRefresh && lastLatitude == latitude && lastLongitude == longitude && _uiState.value is SearchUiState.Success) {
            return
        }

        lastLatitude = latitude
        lastLongitude = longitude

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            when (val result = getNearbyStadiumsUseCase(latitude, longitude, radiusKm)) {
                is Resource.Success -> {
                    _uiState.value = SearchUiState.Success(result.data)
                }
                is Resource.Error -> {
                    _uiState.value = SearchUiState.Error(result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = SearchUiState.Loading
                }
            }
        }
    }
}
