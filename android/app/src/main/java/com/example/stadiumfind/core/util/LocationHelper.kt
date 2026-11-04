package com.example.stadiumfind.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.util.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Helper class cung cấp vị trí hiện tại thông qua FusedLocationProviderClient.
 * Sử dụng CurrentLocationRequest hiện đại để lấy vị trí thời gian thực chính xác.
 */
@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    companion object {
        /** Vị trí mặc định: Trung tâm Hà Nội (Hồ Hoàn Kiếm) */
        val DEFAULT_LOCATION = GeoPoint(21.0285, 105.8542)

        /** Zoom mặc định khi hiện bản đồ */
        const val DEFAULT_ZOOM = 15.0
    }

    /**
     * Kiểm tra quyền Location đã được cấp chưa.
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Kiểm tra phần cứng GPS hoặc Network Location trên thiết bị có đang được BẬT hay không.
     */
    fun isLocationServiceEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Lấy vị trí hiện tại thời gian thực.
     * Sử dụng CurrentLocationRequest (API chuẩn) với timeout 8 giây để GPS kịp kết nối.
     * Tự động fallback sang lastLocation hoặc LocationManager nếu cần.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoPoint? {
        if (!hasLocationPermission()) return null

        // 1. Thử lấy vị trí thời gian thực mới nhất thông qua CurrentLocationRequest (Google Play Services)
        val freshLocation = withTimeoutOrNull(8000L) {
            suspendCancellableCoroutine { cont ->
                val cts = CancellationTokenSource()
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(10_000L) // Chấp nhận vị trí trong vòng 10 giây trở lại
                    .setDurationMillis(7500L)
                    .build()

                fusedClient.getCurrentLocation(request, cts.token)
                    .addOnSuccessListener { location: Location? ->
                        if (location != null && cont.isActive) {
                            cont.resume(GeoPoint(location.latitude, location.longitude))
                        } else if (cont.isActive) {
                            cont.resume(null)
                        }
                    }
                    .addOnFailureListener {
                        if (cont.isActive) {
                            cont.resume(null)
                        }
                    }

                cont.invokeOnCancellation {
                    cts.cancel()
                }
            }
        }

        if (freshLocation != null) {
            return freshLocation
        }

        // 2. Fallback: Lấy lastLocation từ FusedLocationProviderClient
        val lastLocation = withTimeoutOrNull(2000L) {
            suspendCancellableCoroutine { cont ->
                fusedClient.lastLocation
                    .addOnSuccessListener { loc: Location? ->
                        if (loc != null && cont.isActive) {
                            cont.resume(GeoPoint(loc.latitude, loc.longitude))
                        } else if (cont.isActive) {
                            cont.resume(null)
                        }
                    }
                    .addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
            }
        }

        if (lastLocation != null) {
            return lastLocation
        }

        // 3. Fallback cuối cùng: LocationManager hệ thống trực tiếp
        return getSystemLocationFallback()
    }

    @SuppressLint("MissingPermission")
    private fun getSystemLocationFallback(): GeoPoint? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
        for (provider in providers) {
            if (locationManager.isProviderEnabled(provider)) {
                val loc = locationManager.getLastKnownLocation(provider)
                if (loc != null) {
                    return GeoPoint(loc.latitude, loc.longitude)
                }
            }
        }
        return null
    }
}
