package com.example.stadiumfind.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stadiumfind.features.booking.data.local.dao.TicketDao
import com.example.stadiumfind.features.booking.data.local.entity.TicketEntity

/**
 * Cơ sở dữ liệu Room cục bộ cho ứng dụng StadiumFind.
 * Quản lý các bảng Entity cục bộ, hỗ trợ lưu trữ Offline-First.
 */
@Database(
    entities = [TicketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao
}
