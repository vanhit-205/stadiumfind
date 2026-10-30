package com.example.stadiumfind.features.match.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.match.domain.model.MatchParticipant
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyMatchesUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(
        status: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Flow<Resource<List<MatchParticipant>>> {
        return repository.getMyMatches(status = status, page = page, size = size)
    }
}
