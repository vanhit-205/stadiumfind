package com.example.stadiumfind.di

import android.content.Context
import androidx.room.Room
import com.example.stadiumfind.core.database.AppDatabase
import com.example.stadiumfind.features.booking.data.local.dao.TicketDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module cung cấp thể hiện Singleton của AppDatabase và các DAOs liên quan.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "stadiumfind.db"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTicketDao(database: AppDatabase): TicketDao {
        return database.ticketDao()
    }
}
