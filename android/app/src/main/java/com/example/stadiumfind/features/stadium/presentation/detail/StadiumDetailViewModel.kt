package com.example.stadiumfind.features.stadium.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.usecase.GetStadiumDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel cho màn hình Chi tiết Sân bóng.
 * Quản lý trạng thái UI thông qua StateFlow<Resource<StadiumItem>>.
 */
@HiltViewModel
class StadiumDetailViewModel @Inject constructor(
    private val getStadiumDetailUseCase: GetStadiumDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stadiumId: Long = savedStateHandle.get<Long>(StadiumDetailFragment.ARG_STADIUM_ID) ?: 0L

    private val _detailState = MutableStateFlow<Resource<StadiumItem>>(Resource.Loading)
    val detailState: StateFlow<Resource<StadiumItem>> = _detailState.asStateFlow()

    init {
        if (stadiumId != 0L) {
            loadStadiumDetail(stadiumId)
        } else {
            _detailState.value = Resource.Error("Không tìm thấy mã sân bóng")
        }
    }

    fun loadStadiumDetail(id: Long = stadiumId) {
        val targetId = if (id != 0L) id else stadiumId
        if (targetId == 0L) {
            _detailState.value = Resource.Error("Không tìm thấy mã sân bóng")
            return
        }
        viewModelScope.launch {
            _detailState.value = Resource.Loading
            _detailState.value = getStadiumDetailUseCase(targetId)
        }
    }
}
