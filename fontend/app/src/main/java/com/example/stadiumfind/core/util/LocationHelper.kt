package com.example.stadiumfind.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.util.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Helper class cung cấp vị trí hiện tại thông qua FusedLocationProviderClient.
 * Wrap trong suspend function để dùng với Coroutines.
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
     * Lấy vị trí hiện tại. Trả về null nếu không lấy được.
     * Ưu tiên lastLocation cho nhanh, fallback sang getCurrentLocation nếu null.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): GeoPoint? {
        if (!hasLocationPermission()) return null

        return try {
            kotlinx.coroutines.withTimeoutOrNull(3500L) {
                suspendCancellableCoroutine { cont ->
                    // Thử lastLocation trước (nhanh, không tốn pin)
                    fusedClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            if (location != null && cont.isActive) {
                                cont.resume(GeoPoint(location.latitude, location.longitude))
                            } else if (cont.isActive) {
                                // Fallback: request location 1 lần
                                requestSingleUpdate(cont)
                            }
                        }
                        .addOnFailureListener {
                            if (cont.isActive) {
                                // Fallback
                                requestSingleUpdate(cont)
                            }
                        }
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Request cập nhật vị trí 1 lần duy nhất khi lastLocation trả về null.
     */
    @SuppressLint("MissingPermission")
    private fun requestSingleUpdate(cont: kotlinx.coroutines.CancellableContinuation<GeoPoint?>) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        )
            .setMaxUpdates(1)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedClient.removeLocationUpdates(this)
                val loc = result.lastLocation
                if (loc != null && cont.isActive) {
                    cont.resume(GeoPoint(loc.latitude, loc.longitude))
                } else if (cont.isActive) {
                    cont.resume(null)
                }
            }
        }

        fusedClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        cont.invokeOnCancellation {
            fusedClient.removeLocationUpdates(callback)
        }
    }
}
