package com.example.stadiumfind.features.match.data.remote

import com.example.stadiumfind.core.network.BaseResponse
import com.example.stadiumfind.core.network.PageResponse
import com.example.stadiumfind.features.match.data.remote.dto.MatchParticipantDto
import com.example.stadiumfind.features.match.data.remote.dto.MatchParticipantRequestDto
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostCreateRequestDto
import com.example.stadiumfind.features.match.data.remote.dto.MatchPostDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchApiService {

    /**
     * Lấy danh sách bài đăng tìm kèo có hỗ trợ lọc và phân trang (Khớp với MatchPostController: @GetMapping("/api/matches")).
     */
    @GET("matches")
    suspend fun getMatchPosts(
        @Query("sportType") sportType: String? = null,
        @Query("status") status: String? = "OPEN",
        @Query("fromDate") fromDate: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<MatchPostDto>>>

    @GET("matches/{id}")
    suspend fun getMatchPostById(
        @Path("id") id: Long
    ): Response<BaseResponse<MatchPostDto>>

    @POST("matches")
    suspend fun createMatchPost(
        @Body request: MatchPostCreateRequestDto
    ): Response<BaseResponse<MatchPostDto>>

    @POST("matches/{matchPostId}/join")
    suspend fun joinMatch(
        @Path("matchPostId") matchPostId: Long,
        @Body request: MatchParticipantRequestDto
    ): Response<BaseResponse<MatchParticipantDto>>

    @GET("matches/{matchPostId}/participants")
    suspend fun getParticipants(
        @Path("matchPostId") matchPostId: Long
    ): Response<BaseResponse<List<MatchParticipantDto>>>

    @PUT("match-participants/{participantId}/status")
    suspend fun updateParticipantStatus(
        @Path("participantId") participantId: Long,
        @Query("status") status: String
    ): Response<BaseResponse<MatchParticipantDto>>

    @GET("match-participants/my-matches")
    suspend fun getMyMatches(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<PageResponse<MatchParticipantDto>>>

    @DELETE("matches/{matchPostId}/leave")
    suspend fun leaveMatch(
        @Path("matchPostId") matchPostId: Long
    ): Response<BaseResponse<Unit>>
}
