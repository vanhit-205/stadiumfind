package com.example.stadiumfind.features.stadium.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import javax.inject.Inject

/**
 * UseCase lấy danh sách các cụm sân bóng phục vụ việc chọn sân (Stadium Picker).
 */
class GetStadiumsListUseCase @Inject constructor(
    private val repository: StadiumRepository
) {
    suspend operator fun invoke(keyword: String? = null): Resource<List<StadiumItem>> {
        return repository.getStadiumsList(keyword)
    }
}
