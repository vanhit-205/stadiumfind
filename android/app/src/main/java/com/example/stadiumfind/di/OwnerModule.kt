package com.example.stadiumfind.di

import com.example.stadiumfind.features.owner.data.remote.OwnerStatisticsApiService
import com.example.stadiumfind.features.owner.data.repository.OwnerStatisticsRepositoryImpl
import com.example.stadiumfind.features.owner.domain.repository.OwnerStatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OwnerModule {

    @Binds
    @Singleton
    abstract fun bindOwnerStatisticsRepository(
        impl: OwnerStatisticsRepositoryImpl
    ): OwnerStatisticsRepository

    companion object {
        @Provides
        @Singleton
        fun provideOwnerStatisticsApiService(retrofit: Retrofit): OwnerStatisticsApiService {
            return retrofit.create(OwnerStatisticsApiService::class.java)
        }
    }
}
