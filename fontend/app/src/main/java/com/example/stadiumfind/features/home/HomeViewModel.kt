package com.example.stadiumfind.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.usecase.GetStadiumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel cho màn hình Home, quản lý dữ liệu phân trang sân bóng Paging 3
 * và bộ lọc tìm kiếm theo Clean Architecture.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStadiumsUseCase: GetStadiumsUseCase
) : ViewModel() {

    private val _searchFilter = MutableStateFlow(SearchFilter())

    @OptIn(ExperimentalCoroutinesApi::class)
    val stadiumsFlow: Flow<PagingData<StadiumItem>> = _searchFilter
        .flatMapLatest { filter ->
            getStadiumsUseCase(
                keyword = filter.keyword,
                provinceCity = filter.provinceCity
            )
        }
        .cachedIn(viewModelScope)

    fun setFilter(keyword: String? = null, provinceCity: String? = null) {
        _searchFilter.value = SearchFilter(keyword = keyword, provinceCity = provinceCity)
    }

    data class SearchFilter(
        val keyword: String? = null,
        val provinceCity: String? = null
    )
}
