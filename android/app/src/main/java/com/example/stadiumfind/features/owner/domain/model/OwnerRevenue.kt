package com.example.stadiumfind.features.owner.domain.model

data class OwnerRevenue(
    val totalRevenue: Double = 0.0,
    val totalBookings: Long = 0,
    val items: List<RevenueItem> = emptyList()
)

data class RevenueItem(
    val period: String,
    val revenue: Double = 0.0,
    val bookingCount: Long = 0
)
