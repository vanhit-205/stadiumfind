package com.example.stadiumfind.features.owner.domain.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.features.owner.domain.model.OwnerRevenue

interface OwnerStatisticsRepository {
    suspend fun getRevenueStatistics(
        stadiumId: Long? = null,
        type: String = "DAILY",
        year: Int? = null,
        month: Int? = null
    ): Resource<OwnerRevenue>
}
