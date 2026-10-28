package com.example.stadiumfind.di

import com.example.stadiumfind.features.match.data.remote.MatchApiService
import com.example.stadiumfind.features.match.data.repository.MatchRepositoryImpl
import com.example.stadiumfind.features.match.domain.repository.MatchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MatchModule {

    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        impl: MatchRepositoryImpl
    ): MatchRepository

    companion object {

        @Provides
        @Singleton
        fun provideMatchApiService(retrofit: Retrofit): MatchApiService {
            return retrofit.create(MatchApiService::class.java)
        }
    }
}
