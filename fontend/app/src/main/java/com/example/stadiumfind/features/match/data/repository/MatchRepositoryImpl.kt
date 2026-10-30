package com.example.stadiumfind.features.match.data.repository

import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.booking.data.remote.BookingApiService
import com.example.stadiumfind.features.booking.domain.model.CourtItem
import com.example.stadiumfind.features.match.data.remote.MatchApiService
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostCreateRequestDto
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostDto
import com.example.stadiumfind.features.match.domain.model.MatchPost
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import com.example.stadiumfind.features.stadium.data.remote.StadiumApiService
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val apiService: MatchApiService,
    private val stadiumApiService: StadiumApiService,
    private val bookingApiService: BookingApiService,
    private val userPreferencesDataStore: UserPreferencesDataStore
) : MatchRepository {

    override fun getOpenMatchPosts(sportType: String?, page: Int, size: Int): Flow<Resource<List<MatchPost>>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            apiService.getMatchPosts(
                sportType = if (sportType == "ALL") null else sportType,
                status = "OPEN",
                page = page,
                size = size
            )
        }
        when (result) {
            is Resource.Success -> {
                val list = result.data.content.map { it.toDomain() }
                emit(Resource.Success(list))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun createMatchPost(request: MatchPostCreateRequestDto): Flow<Resource<MatchPost>> = flow {
        emit(Resource.Loading)

        // Tự động gắn creatorId từ DataStore nếu request chưa có
        val currentUserId = userPreferencesDataStore.userId.firstOrNull()
        val finalRequest = if (request.creatorId == null && currentUserId != null) {
            request.copy(creatorId = currentUserId)
        } else {
            request
        }

        val result = safeApiCall {
            apiService.createMatchPost(finalRequest)
        }
        when (result) {
            is Resource.Success -> {
                emit(Resource.Success(result.data.toDomain()))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun joinMatch(matchPostId: Long, note: String?): Flow<Resource<com.example.stadiumfind.features.match.domain.model.MatchParticipant>> = flow {
        emit(Resource.Loading)
        val currentUserId = userPreferencesDataStore.userId.firstOrNull()
        val request = com.example.stadiumfind.features.match.data.remote.dto.MatchParticipantRequestDto(
            matchPostId = matchPostId,
            userId = currentUserId,
            note = note
        )
        val result = safeApiCall {
            apiService.joinMatch(matchPostId, request)
        }
        when (result) {
            is Resource.Success -> emit(Resource.Success(result.data.toDomain()))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun getParticipants(matchPostId: Long): Flow<Resource<List<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            apiService.getParticipants(matchPostId)
        }
        when (result) {
            is Resource.Success -> {
                val list = result.data.map { it.toDomain() }
                emit(Resource.Success(list))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun updateParticipantStatus(
        participantId: Long,
        status: String
    ): Flow<Resource<com.example.stadiumfind.features.match.domain.model.MatchParticipant>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            apiService.updateParticipantStatus(participantId, status)
        }
        when (result) {
            is Resource.Success -> emit(Resource.Success(result.data.toDomain()))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun getMyMatches(
        status: String?,
        page: Int,
        size: Int
    ): Flow<Resource<List<com.example.stadiumfind.features.match.domain.model.MatchParticipant>>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            apiService.getMyMatches(
                status = if (status.isNullOrBlank() || status == "ALL") null else status,
                page = page,
                size = size
            )
        }
        when (result) {
            is Resource.Success -> {
                val list = result.data.content.map { it.toDomain() }
                emit(Resource.Success(list))
            }
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override fun leaveMatch(matchPostId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        val result = safeApiCall {
            apiService.leaveMatch(matchPostId)
        }
        when (result) {
            is Resource.Success -> emit(Resource.Success(Unit))
            is Resource.Error -> emit(Resource.Error(result.message, result.code))
            is Resource.Loading -> emit(Resource.Loading)
        }
    }

    override suspend fun getStadiums(): Resource<List<StadiumItem>> {
        val result = safeApiCall { stadiumApiService.getStadiums(page = 0, size = 50) }
        return when (result) {
            is Resource.Success -> {
                val list = result.data.content.map { dto ->
                    StadiumItem(
                        id = dto.id,
                        name = dto.name,
                        address = dto.address,
                        imageUrl = dto.imageUrl,
                        rating = dto.rating ?: 5.0,
                        minPricePerHour = dto.minPricePerHour ?: 200000.0,
                        sportTypes = listOf("Bóng đá")
                    )
                }
                Resource.Success(list)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getCourtsByStadium(stadiumId: Long): Resource<List<CourtItem>> {
        val result = safeApiCall { bookingApiService.getCourtsByStadium(stadiumId) }
        return when (result) {
            is Resource.Success -> {
                val courts = result.data.mapIndexed { index, dto ->
                    dto.toDomain(isSelected = (index == 0))
                }
                Resource.Success(courts)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    private fun com.example.stadiumfind.features.match.data.remote.dto.MatchParticipantDto.toDomain(): com.example.stadiumfind.features.match.domain.model.MatchParticipant {
        return com.example.stadiumfind.features.match.domain.model.MatchParticipant(
            id = id,
            matchPostId = matchPostId,
            userId = userId,
            userName = userName ?: "Thành viên",
            userAvatar = userAvatar,
            userPhoneNumber = userPhoneNumber,
            status = status,
            note = note,
            joinedAt = joinedAt,
            matchTitle = matchTitle,
            sportType = sportType,
            playDate = playDate,
            startTime = startTime,
            endTime = endTime,
            stadiumName = stadiumName,
            courtName = courtName,
            maxParticipants = maxParticipants ?: 10,
            currentParticipants = currentParticipants ?: 1,
            pricePerPerson = pricePerPerson ?: 0.0,
            matchStatus = matchStatus,
            creatorName = creatorName,
            creatorAvatar = creatorAvatar,
            creatorPhone = creatorPhone
        )
    }

    private fun MatchPostDto.toDomain(): MatchPost {
        return MatchPost(
            id = id,
            creatorId = creatorId,
            creatorName = creatorName ?: "Thành viên",
            creatorAvatar = creatorAvatar,
            courtId = courtId,
            courtName = courtName ?: "Sân bóng",
            stadiumId = stadiumId,
            stadiumName = stadiumName ?: "Cụm sân thể thao",
            bookingId = bookingId,
            title = title,
            description = description ?: "",
            sportType = sportType,
            playDate = playDate,
            startTime = startTime,
            endTime = endTime,
            maxParticipants = maxParticipants ?: 10,
            currentParticipants = currentParticipants ?: 1,
            pricePerPerson = pricePerPerson ?: 0.0,
            status = status
        )
    }
}
