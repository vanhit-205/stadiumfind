package com.example.stadiumfind.core.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.features.profile.domain.usecase.UpdateFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

object FcmHelper {

    private const val TAG = "FcmHelper"

    /**
     * Xin quyền POST_NOTIFICATIONS cho Android 13 (API 33)+
     */
    fun checkAndRequestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1001)
            }
        }
    }

    /**
     * Lấy FCM token hiện tại và đồng bộ lên server nếu người dùng đã đăng nhập.
     */
    fun syncFcmTokenToServer(
        userPreferences: UserPreferencesDataStore,
        updateFcmTokenUseCase: UpdateFcmTokenUseCase
    ) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "FCM Token fetched: $token")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val accessToken = userPreferences.accessToken.firstOrNull()
                        if (!accessToken.isNullOrBlank() && !token.isNullOrBlank()) {
                            updateFcmTokenUseCase(token)
                            Log.d(TAG, "Updated FCM Token to backend successfully")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to update FCM token to backend: ${e.message}", e)
                    }
                }
            }
    }
}
