package com.example.stadiumfind.di

import com.example.stadiumfind.features.stadium.data.remote.StadiumApiService
import com.example.stadiumfind.features.stadium.data.repository.StadiumRepositoryImpl
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StadiumModule {

    @Binds
    @Singleton
    abstract fun bindStadiumRepository(
        impl: StadiumRepositoryImpl
    ): StadiumRepository

    companion object {

        @Provides
        @Singleton
        fun provideStadiumApiService(retrofit: Retrofit): StadiumApiService {
            return retrofit.create(StadiumApiService::class.java)
        }
    }
}
