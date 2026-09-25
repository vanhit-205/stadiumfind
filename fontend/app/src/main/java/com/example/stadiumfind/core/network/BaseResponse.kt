package com.example.stadiumfind.core.network

import com.google.gson.annotations.SerializedName

/**
 * Wrapper chuẩn hoá cho MỌI response từ backend Spring Boot.
 *
 * Backend phải trả về JSON theo cấu trúc:
 * ```json
 * {
 *   "success": true,
 *   "message": "Login successful",
 *   "data": { ... }
 * }
 * ```
 *
 * Cách dùng trong Retrofit ApiService:
 * ```kotlin
 * @POST("auth/login")
 * suspend fun login(@Body request: LoginRequest): Response<BaseResponse<LoginResponse>>
 * ```
 *
 * @param T Kiểu dữ liệu của field "data" trong JSON response
 */
data class BaseResponse<T>(

    /** true nếu request thành công, false nếu có lỗi logic phía server */
    @SerializedName("success")
    val success: Boolean,

    /** Thông báo từ server (mô tả kết quả hoặc lỗi) */
    @SerializedName("message")
    val message: String?,

    /** Dữ liệu thực sự — null khi success = false hoặc không có data */
    @SerializedName("data")
    val data: T?
)
