package com.example.stadiumfind.features.stadium.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import javax.inject.Inject

/**
 * UseCase lấy thông tin chi tiết một sân bóng theo ID.
 */
class GetStadiumDetailUseCase @Inject constructor(
    private val repository: StadiumRepository
) {
    suspend operator fun invoke(id: Long): Resource<StadiumItem> {
        return repository.getStadiumDetail(id)
    }
}
