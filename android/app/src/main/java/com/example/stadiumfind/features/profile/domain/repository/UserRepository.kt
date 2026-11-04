package com.example.stadiumfind.features.profile.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.profile.domain.model.UserProfile

interface UserRepository {
    suspend fun getProfile(): Resource<UserProfile>
    suspend fun updateProfile(fullName: String, phoneNumber: String?, avatarUrl: String?): Resource<UserProfile>
}
