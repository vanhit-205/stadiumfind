package com.example.stadiumfind.features.notification.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.core.network.PageResponse
import com.example.stadiumfind.features.notification.data.remote.dto.NotificationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<NotificationDto>>>

    @GET("notifications/unread")
    suspend fun getUnreadNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<NotificationDto>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<BaseResponse<Long>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") id: Long
    ): Response<BaseResponse<Unit>>

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<BaseResponse<Unit>>
}
