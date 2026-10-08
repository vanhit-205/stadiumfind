package com.example.stadiumfind.core.util

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import com.example.stadiumfind.R

/**
 * Extension function load ảnh sân bóng dùng Coil với hiệu ứng crossfade,
 * cache chính sách disk/memory và placeholder chuẩn Dark Mode.
 */
fun ImageView.loadStadiumImage(
    url: String?,
    placeholderRes: Int = R.drawable.ic_stadium_placeholder,
    errorRes: Int = R.drawable.ic_stadium_placeholder
) {
    this.load(url) {
        crossfade(true)
        crossfade(300)
        placeholder(placeholderRes)
        error(errorRes)
        diskCachePolicy(CachePolicy.ENABLED)
        memoryCachePolicy(CachePolicy.ENABLED)
    }
}
