package com.example.stadiumfind.core.network

import retrofit2.Response
import java.io.IOException

/**
 * Hàm extension bọc một Retrofit API call trong try/catch,
 * tự động chuyển đổi [Response]<[BaseResponse]<T>> thành [Resource]<T>.
 *
 * Xử lý toàn bộ các loại lỗi có thể xảy ra:
 * - **Lỗi mạng** (IOException): không có internet, timeout, DNS fail
 * - **Lỗi HTTP 4xx/5xx**: server trả về error code
 * - **Lỗi logic**: body null, success = false từ server
 * - **Lỗi parse**: JSON không đúng format, exception không mong muốn
 *
 * Cách dùng trong Repository:
 * ```kotlin
 * override suspend fun login(email: String, password: String): Resource<LoginResponse> {
 *     return withContext(ioDispatcher) {
 *         safeApiCall { apiService.login(LoginRequest(email, password)) }
 *     }
 * }
 * ```
 *
 * @param T Kiểu dữ liệu khi thành công (field "data" trong BaseResponse)
 * @param apiCall Lambda chứa Retrofit suspend function call
 * @return [Resource.Success] với data, hoặc [Resource.Error] với message phù hợp
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<BaseResponse<T>>
): Resource<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.success && body.data != null) {
                Resource.Success(body.data)
            } else {
                Resource.Error(
                    message = body?.message ?: "Có lỗi xảy ra, vui lòng thử lại.",
                    code = response.code()
                )
            }
        } else {
            // Server trả về HTTP error (4xx, 5xx)
            val errorMessage = when (response.code()) {
                400 -> "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại."
                401 -> "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."
                403 -> "Bạn không có quyền thực hiện thao tác này."
                404 -> "Không tìm thấy dữ liệu yêu cầu."
                409 -> "Dữ liệu đã tồn tại. Vui lòng kiểm tra lại."
                422 -> "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các trường."
                500 -> "Lỗi máy chủ. Vui lòng thử lại sau."
                503 -> "Dịch vụ tạm thời không khả dụng. Vui lòng thử lại sau."
                else -> "Lỗi không xác định (${response.code()})."
            }
            Resource.Error(message = errorMessage, code = response.code())
        }
    } catch (e: IOException) {
        // Mất mạng, timeout, DNS fail
        Resource.Error(message = "Không có kết nối mạng. Vui lòng kiểm tra lại.")
    } catch (e: Exception) {
        // Lỗi parse JSON hoặc lỗi không mong muốn khác
        Resource.Error(message = e.localizedMessage ?: "Có lỗi không xác định xảy ra.")
    }
}
