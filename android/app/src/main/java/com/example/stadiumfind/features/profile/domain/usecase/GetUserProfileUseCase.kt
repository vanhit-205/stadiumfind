package com.example.stadiumfind.features.profile.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.profile.domain.model.UserProfile
import com.example.stadiumfind.features.profile.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Resource<UserProfile> {
        return userRepository.getProfile()
    }
}
