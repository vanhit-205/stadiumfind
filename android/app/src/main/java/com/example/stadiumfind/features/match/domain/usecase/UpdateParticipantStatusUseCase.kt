package com.example.stadiumfind.features.match.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.match.domain.model.MatchParticipant
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateParticipantStatusUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(participantId: Long, status: String): Flow<Resource<MatchParticipant>> {
        return repository.updateParticipantStatus(participantId, status)
    }
}
