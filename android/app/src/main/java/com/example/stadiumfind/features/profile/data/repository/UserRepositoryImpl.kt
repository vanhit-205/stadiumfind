package com.example.stadiumfind.features.profile.data.repository

import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.profile.data.remote.UserApiService
import com.example.stadiumfind.features.profile.data.remote.dto.UserUpdateRequestDto
import com.example.stadiumfind.features.profile.domain.model.UserProfile
import com.example.stadiumfind.features.profile.domain.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val userPreferences: UserPreferencesDataStore
) : UserRepository {

    override suspend fun getProfile(): Resource<UserProfile> {
        val result = safeApiCall { apiService.getProfile() }
        return when (result) {
            is Resource.Success -> {
                val profile = result.data.toDomain()
                // Cập nhật lại thông tin mới nhất vào DataStore
                val currentAccessToken = userPreferences.accessToken.firstOrNull() ?: ""
                val currentRefreshToken = userPreferences.refreshToken.firstOrNull()
                userPreferences.saveAuthData(
                    accessToken = currentAccessToken,
                    refreshToken = currentRefreshToken,
                    userId = profile.id,
                    username = profile.username,
                    email = profile.email,
                    avatarUrl = profile.avatarUrl
                )
                Resource.Success(profile)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun updateProfile(
        fullName: String,
        phoneNumber: String?,
        avatarUrl: String?
    ): Resource<UserProfile> {
        val result = safeApiCall {
            apiService.updateProfile(
                UserUpdateRequestDto(
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    avatarUrl = avatarUrl
                )
            )
        }
        return when (result) {
            is Resource.Success -> Resource.Success(result.data.toDomain())
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
