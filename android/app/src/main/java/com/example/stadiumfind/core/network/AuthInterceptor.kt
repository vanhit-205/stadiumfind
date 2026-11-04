package com.example.stadiumfind.core.network

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import javax.inject.Inject

/**
 * OkHttp Interceptor tự động đính kèm JWT vào header Authorization.
 * Chỉ đính token nếu request chưa có header Authorization.
 */
class AuthInterceptor @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Nếu đã có header Authorization (ví dụ: refresh token call) thì bỏ qua
        if (original.header("Authorization") != null) {
            return chain.proceed(original)
        }

        val token = runBlocking {
            dataStore.accessToken.firstOrNull()
        }

        val request = if (!token.isNullOrEmpty()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        return chain.proceed(request)
    }
}
