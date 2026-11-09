package com.example.stadiumfind.features.owner.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OwnerRevenueResponseDto(
    @SerializedName("totalRevenue")
    val totalRevenue: Double = 0.0,

    @SerializedName("totalBookings")
    val totalBookings: Long = 0,

    @SerializedName("items")
    val items: List<RevenueItemDto> = emptyList()
)

data class RevenueItemDto(
    @SerializedName("period")
    val period: String,

    @SerializedName("revenue")
    val revenue: Double = 0.0,

    @SerializedName("bookingCount")
    val bookingCount: Long = 0
)
