package com.example.stadiumfind.features.auth.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.auth.domain.model.AuthResult

interface AuthRepository {

    suspend fun login(usernameOrEmail: String, password: String): Resource<AuthResult>

    suspend fun register(username: String, email: String, password: String): Resource<AuthResult>
}
