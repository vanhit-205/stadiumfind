package com.example.stadiumfind.features.match.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostCreateRequestDto
import com.example.stadiumfind.features.match.domain.model.MatchPost
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getOpenMatchPosts(sportType: String? = null, page: Int = 0, size: Int = 20): Flow<Resource<List<MatchPost>>>
    fun createMatchPost(request: MatchPostCreateRequestDto): Flow<Resource<MatchPost>>
    fun joinMatch(matchPostId: Long, note: String? = null): Flow<Resource<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>
    fun getParticipants(matchPostId: Long): Flow<Resource<List<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>>
    fun updateParticipantStatus(participantId: Long, status: String): Flow<Resource<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>
    fun getMyMatches(status: String? = null, page: Int = 0, size: Int = 20): Flow<Resource<List<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>>
    fun leaveMatch(matchPostId: Long): Flow<Resource<Unit>>
    suspend fun getStadiums(): Resource<List<StadiumItem>>
    suspend fun getCourtsByStadium(stadiumId: Long): Resource<List<CourtItem>>
}
