package com.example.stadiumfind.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Quản lý lưu trữ token JWT và thông tin user cục bộ bằng DataStore.
 * Inject vào ViewModel hoặc Repository qua Hilt.
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_AVATAR_URL = stringPreferencesKey("avatar_url")
    }

    // --- READ ---

    val accessToken: Flow<String?> = context.dataStore.data.map { it[KEY_ACCESS_TOKEN] }

    val refreshToken: Flow<String?> = context.dataStore.data.map { it[KEY_REFRESH_TOKEN] }

    val userId: Flow<Long?> = context.dataStore.data.map { it[KEY_USER_ID] }

    val username: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map {
        !it[KEY_ACCESS_TOKEN].isNullOrEmpty()
    }

    // --- WRITE ---

    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String?,
        userId: Long?,
        username: String?,
        email: String?,
        avatarUrl: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            if (refreshToken != null) prefs[KEY_REFRESH_TOKEN] = refreshToken
            if (userId != null) prefs[KEY_USER_ID] = userId
            if (username != null) prefs[KEY_USERNAME] = username
            if (email != null) prefs[KEY_EMAIL] = email
            if (avatarUrl != null) prefs[KEY_AVATAR_URL] = avatarUrl
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    /** Lấy token đồng bộ cho Interceptor – dùng runBlocking nội bộ */
    suspend fun getAccessTokenOnce(): String? {
        var token: String? = null
        context.dataStore.data.collect {
            token = it[KEY_ACCESS_TOKEN]
            return@collect
        }
        return token
    }
}
