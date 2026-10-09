package com.example.stadiumfind.core.network

import com.google.gson.annotations.SerializedName

/**
 * Model ánh xạ cho đối tượng Page<T> của Spring Data.
 */
data class PageResponse<T>(
    @SerializedName("content")
    val content: List<T> = emptyList(),

    @SerializedName("totalElements")
    val totalElements: Long = 0,

    @SerializedName("totalPages")
    val totalPages: Int = 0,

    @SerializedName("number")
    val number: Int = 0,

    @SerializedName("size")
    val size: Int = 10,

    @SerializedName("numberOfElements")
    val numberOfElements: Int = 0,

    @SerializedName("first")
    val first: Boolean = true,

    @SerializedName("last")
    val last: Boolean = true,

    @SerializedName("empty")
    val empty: Boolean = true
)
