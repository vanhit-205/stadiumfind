package com.example.stadiumfind.di

import com.example.stadiumfind.features.notification.data.remote.NotificationApiService
import com.example.stadiumfind.features.notification.data.repository.NotificationRepositoryImpl
import com.example.stadiumfind.features.notification.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService {
            return retrofit.create(NotificationApiService::class.java)
        }
    }
}
