package com.example.stadiumfind.features.auth.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.auth.domain.model.AuthResult
import com.example.stadiumfind.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(usernameOrEmail: String, password: String): Resource<AuthResult> {
        return repository.login(usernameOrEmail, password)
    }
}
