package com.example.stadiumfind.features.match.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.match.domain.model.MatchParticipant
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchParticipantsUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(matchPostId: Long): Flow<Resource<List<MatchParticipant>>> {
        return repository.getParticipants(matchPostId)
    }
}
