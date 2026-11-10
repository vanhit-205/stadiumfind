package com.example.stadiumfind.features.profile.domain.usecase

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.profile.domain.repository.UserRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(fcmToken: String): Resource<Unit> {
        return userRepository.updateFcmToken(fcmToken)
    }
}
