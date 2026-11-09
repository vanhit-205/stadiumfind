package com.example.stadiumfind.features.booking.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.stadiumfind.features.booking.data.local.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) thực hiện các thao tác CRUD vé đặt sân trong Room DB.
 */
@Dao
interface TicketDao {

    @Upsert
    suspend fun upsertTickets(tickets: List<TicketEntity>)

    @Upsert
    suspend fun upsertTicket(ticket: TicketEntity)

    @Query("SELECT * FROM tickets ORDER BY bookingDate DESC, startTime DESC")
    suspend fun getAllTickets(): List<TicketEntity>

    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY bookingDate DESC, startTime DESC")
    suspend fun getTicketsByUserId(userId: Long): List<TicketEntity>

    @Query("SELECT * FROM tickets WHERE userId = :userId ORDER BY bookingDate DESC, startTime DESC")
    fun getTicketsByUserIdFlow(userId: Long): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets ORDER BY bookingDate DESC, startTime DESC")
    fun getAllTicketsFlow(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId LIMIT 1")
    suspend fun getTicketById(ticketId: Long): TicketEntity?

    @Query("SELECT * FROM tickets WHERE id = :ticketId LIMIT 1")
    fun getTicketByIdFlow(ticketId: Long): Flow<TicketEntity?>

    @Query("DELETE FROM tickets WHERE id = :ticketId")
    suspend fun deleteTicketById(ticketId: Long)

    @Query("DELETE FROM tickets")
    suspend fun clearAll()
}
