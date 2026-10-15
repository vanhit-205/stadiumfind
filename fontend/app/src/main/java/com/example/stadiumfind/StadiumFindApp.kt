package com.example.stadiumfind

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class StadiumFindApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("osmdroid_prefs", MODE_PRIVATE)
        Configuration.getInstance().load(this, prefs)
        Configuration.getInstance().userAgentValue = "StadiumFindApp/1.0 (Android; contact@stadiumfind.com)"
        Configuration.getInstance().save(this, prefs)

        // Xóa sạch tile cache cũ (nơi lưu các tile PNG '403 Access Blocked' trước đó)
        try {
            val tileCacheDir = Configuration.getInstance().osmdroidTileCache
            if (tileCacheDir != null && tileCacheDir.exists()) {
                tileCacheDir.deleteRecursively()
                Log.d("StadiumFindApp", "Đã dọn dẹp tile cache cũ của osmdroid.")
            }
        } catch (e: Exception) {
            Log.w("StadiumFindApp", "Không thể xóa tile cache: ${e.message}")
        }
    }
}
