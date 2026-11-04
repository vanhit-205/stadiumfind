package com.example.stadiumfind.di

import com.example.stadiumfind.features.booking.data.remote.BookingApiService
import com.example.stadiumfind.features.booking.data.repository.BookingRepositoryImpl
import com.example.stadiumfind.features.booking.domain.repository.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingModule {

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        impl: BookingRepositoryImpl
    ): BookingRepository

    companion object {

        @Provides
        @Singleton
        fun provideBookingApiService(retrofit: Retrofit): BookingApiService {
            return retrofit.create(BookingApiService::class.java)
        }
    }
}
