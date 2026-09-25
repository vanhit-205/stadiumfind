package com.example.stadiumfind.core.network

/**
 * Sealed class đại diện cho 3 trạng thái của một tác vụ bất đồng bộ (API call, DB query...).
 *
 * Cách dùng trong ViewModel:
 * ```
 * private val _state = MutableStateFlow<Resource<User>>(Resource.Loading)
 * val state: StateFlow<Resource<User>> = _state.asStateFlow()
 * ```
 *
 * Cách collect trong Fragment:
 * ```
 * when (resource) {
 *     is Resource.Loading -> showProgressBar()
 *     is Resource.Success -> showData(resource.data)
 *     is Resource.Error   -> showError(resource.message)
 * }
 * ```
 *
 * @param T Kiểu dữ liệu khi thành công
 */
sealed class Resource<out T> {

    /** Đang tải dữ liệu — UI nên hiển thị ProgressBar/Shimmer */
    data object Loading : Resource<Nothing>()

    /**
     * Tác vụ thành công — có dữ liệu để hiển thị.
     * @param data Dữ liệu trả về
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Tác vụ thất bại — UI nên hiển thị thông báo lỗi.
     * @param message Nội dung lỗi thân thiện với người dùng
     * @param code HTTP status code (401, 404, 500...), null nếu là lỗi mạng
     */
    data class Error(
        val message: String,
        val code: Int? = null
    ) : Resource<Nothing>()
}
