package com.example.stadiumfind.features.stadium.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.stadiumfind.features.stadium.data.mapper.toDomain
import com.example.stadiumfind.features.stadium.data.remote.StadiumApiService
import com.example.stadiumfind.features.stadium.domain.model.StadiumItem
import java.io.IOException
import retrofit2.HttpException

/**
 * PagingSource cho danh sách sân bóng từ backend Spring Boot.
 * Quản lý logic phân trang bắt đầu từ page = 0 theo chuẩn Spring Pageable.
 */
class StadiumPagingSource(
    private val apiService: StadiumApiService,
    private val keyword: String? = null,
    private val provinceCity: String? = null
) : PagingSource<Int, StadiumItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StadiumItem> {
        val currentPage = params.key ?: 0

        return try {
            val response = apiService.getStadiums(
                page = currentPage,
                size = params.loadSize,
                keyword = keyword,
                provinceCity = provinceCity
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    val pageData = body.data
                    val items = pageData.content.map { it.toDomain() }

                    val isLastPage = pageData.last || items.isEmpty() || (pageData.totalPages > 0 && currentPage >= pageData.totalPages - 1)
                    val prevKey = if (currentPage == 0) null else currentPage - 1
                    val nextKey = if (isLastPage) null else currentPage + 1

                    LoadResult.Page(
                        data = items,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                } else {
                    LoadResult.Error(Exception(body?.message ?: "Lỗi tải dữ liệu sân bóng từ server"))
                }
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StadiumItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
