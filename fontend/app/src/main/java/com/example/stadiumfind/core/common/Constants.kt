package com.example.stadiumfind.core.common

object Constants {
    /**
     * Base URL cho API backend.
     * 10.0.2.2 là địa chỉ IP máy chủ local nhìn từ Android Emulator.
     * Nếu chạy trên thiết bị thật qua Wi-Fi, hãy thay bằng IP máy tính của bạn (ví dụ: 192.168.1.x:8080).
     */
    const val BASE_URL = "http://10.0.2.2:8080/api/"
    const val NETWORK_TIMEOUT = 30L

    const val PREFS_NAME = "stadium_find_prefs"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
}
