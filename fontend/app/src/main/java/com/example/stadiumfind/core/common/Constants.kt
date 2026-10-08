package com.example.stadiumfind.core.common

object Constants {
    /**
     * Base URL cho API backend.
     * Khi cắm cáp USB và chạy `adb reverse tcp:8080 tcp:8080`,
     * điện thoại sẽ gọi trực tiếp qua localhost:8080 mà không phụ thuộc vào IP Wi-Fi.
     */
    const val BASE_URL = "http://localhost:8080/api/"
    const val NETWORK_TIMEOUT = 30L

    const val PREFS_NAME = "stadium_find_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
}
