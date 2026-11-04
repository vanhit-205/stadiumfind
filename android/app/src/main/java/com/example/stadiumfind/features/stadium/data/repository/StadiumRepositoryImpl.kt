package com.example.stadiumfind.features.stadium.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.stadiumfind.core.network.Resource
import com.example.stadiumfind.core.network.safeApiCall
import com.example.stadiumfind.features.stadium.data.mapper.toDomain
import com.example.stadiumfind.features.stadium.data.paging.StadiumPagingSource
import com.example.stadiumfind.features.stadium.data.remote.StadiumApiService
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import com.example.stadiumfind.features.stadium.domain.repository.StadiumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StadiumRepositoryImpl @Inject constructor(
    private val apiService: StadiumApiService
) : StadiumRepository {

    override fun getStadiumsStream(
        keyword: String?,
        provinceCity: String?
    ): Flow<PagingData<StadiumItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = {
                StadiumPagingSource(
                    apiService = apiService,
                    keyword = keyword,
                    provinceCity = provinceCity
                )
            }
        ).flow
    }

    override suspend fun getNearbyStadiums(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Resource<List<StadiumItem>> {
        val result = safeApiCall {
            apiService.getNearbyStadiums(latitude, longitude, radiusKm)
        }
        return when (result) {
            is Resource.Success -> {
                val items = result.data.map { it.toDomain() }
                Resource.Success(items)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getStadiumDetail(id: Long): Resource<StadiumItem> {
        val result = safeApiCall {
            apiService.getStadiumById(id)
        }
        return when (result) {
            is Resource.Success -> {
                val item = result.data.toDomain()
                Resource.Success(item)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }

    override suspend fun getStadiumsList(keyword: String?): Resource<List<StadiumItem>> {
        val result = safeApiCall {
            apiService.getStadiums(page = 0, size = 50, keyword = keyword)
        }
        return when (result) {
            is Resource.Success -> {
                val items = result.data.content.map { it.toDomain() }
                Resource.Success(items)
            }
            is Resource.Error -> Resource.Error(result.message, result.code)
            is Resource.Loading -> Resource.Loading
        }
    }
}
