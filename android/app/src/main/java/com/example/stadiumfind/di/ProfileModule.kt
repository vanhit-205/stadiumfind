package com.example.stadiumfind.di

import com.example.stadiumfind.features.profile.data.remote.UserApiService
import com.example.stadiumfind.features.profile.data.repository.UserRepositoryImpl
import com.example.stadiumfind.features.profile.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    companion object {

        @Provides
        @Singleton
        fun provideUserApiService(retrofit: Retrofit): UserApiService {
            return retrofit.create(UserApiService::class.java)
        }
    }
}
