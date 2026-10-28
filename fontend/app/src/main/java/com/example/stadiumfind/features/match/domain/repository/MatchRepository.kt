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
    suspend fun getStadiums(): Resource<List<StadiumItem>>
    suspend fun getCourtsByStadium(stadiumId: Long): Resource<List<CourtItem>>
}
