package com.example.stadiumfind.features.stadium.domain.usecase

import androidx.paging.PagingData
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy luồng dữ liệu phân trang danh sách sân bóng theo Clean Architecture.
 */
class GetStadiumsUseCase @Inject constructor(
    private val repository: StadiumRepository
) {
    operator fun invoke(
        keyword: String? = null,
        provinceCity: String? = null
    ): Flow<PagingData<StadiumItem>> {
        return repository.getStadiumsStream(
            keyword = keyword,
            provinceCity = provinceCity
        )
    }
}
