package com.example.stadiumfind.features.auth.data.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.di.IoDispatcher
import com.example.stadiumfind.features.auth.data.remote.AuthApiService
import com.example.stadiumfind.features.auth.data.remote.dto.AuthResponseDto
import com.example.stadiumfind.features.auth.data.remote.dto.LoginRequest
import com.example.stadiumfind.features.auth.data.remote.dto.RegisterRequest
import com.example.stadiumfind.features.auth.domain.model.AuthResult
import com.example.stadiumfind.features.auth.domain.model.UserModel
import com.example.stadiumfind.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override suspend fun login(usernameOrEmail: String, password: String): Resource<AuthResult> {
        return withContext(ioDispatcher) {
            val result = safeApiCall {
                apiService.login(LoginRequest(usernameOrEmail, password))
            }
            result.mapToAuthResult()
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(ioDispatcher) {
            val result = safeApiCall {
                apiService.register(RegisterRequest(username, email, password))
            }
            result.mapToAuthResult()
        }
    }

    // --- Mapper ---

    private fun Resource<AuthResponseDto>.mapToAuthResult(): Resource<AuthResult> {
        return when (this) {
            is Resource.Success -> Resource.Success(data.toAuthResult())
            is Resource.Error -> Resource.Error(message, code)
            is Resource.Loading -> Resource.Loading
        }
    }

    private fun AuthResponseDto.toAuthResult() = AuthResult(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        user = user?.let {
            UserModel(
                id = it.id,
                username = it.username,
                email = it.email,
                fullName = it.fullName,
                avatarUrl = it.avatarUrl,
                role = it.role
            )
        }
    )
}
