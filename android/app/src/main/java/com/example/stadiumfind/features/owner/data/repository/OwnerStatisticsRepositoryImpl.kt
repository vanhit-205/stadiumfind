package com.example.stadiumfind.features.owner.data.repository

import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.owner.data.remote.OwnerStatisticsApiService
import com.example.stadiumfind.features.owner.domain.model.OwnerRevenue
import com.example.stadiumfind.features.owner.domain.model.RevenueItem
import com.example.stadiumfind.features.owner.domain.repository.OwnerStatisticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerStatisticsRepositoryImpl @Inject constructor(
    private val apiService: OwnerStatisticsApiService
) : OwnerStatisticsRepository {

    override suspend fun getRevenueStatistics(
        stadiumId: Long?,
        type: String,
        year: Int?,
        month: Int?
    ): Resource<OwnerRevenue> {
        val result = safeApiCall {
            apiService.getRevenueStatistics(stadiumId, type, year, month)
        }
        return when (result) {
            is Resource.Success -> {
                val dto = result.data
                val domain = OwnerRevenue(
                    totalRevenue = dto.totalRevenue,
                    totalBookings = dto.totalBookings,
                    items = dto.items.map {
                        RevenueItem(
                            period = it.period,
                            revenue = it.revenue,
                            bookingCount = it.bookingCount
                        )
                    }
                )
                Resource.Success(domain)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
