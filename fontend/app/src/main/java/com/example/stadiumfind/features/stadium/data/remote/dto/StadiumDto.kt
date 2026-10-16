package com.example.stadiumfind.features.stadium.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object nhận từ API /api/stadiums của backend Spring Boot.
 */
data class StadiumDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("provinceCity")
    val provinceCity: String?,

    @SerializedName("district")
    val district: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("openingTime")
    val openingTime: String?,

    @SerializedName("closingTime")
    val closingTime: String?,

    @SerializedName("imageUrl")
    val imageUrl: String?,

    @SerializedName("rating")
    val rating: Double?,

    @SerializedName("minPricePerHour")
    val minPricePerHour: Double?,

    @SerializedName("totalCourts")
    val totalCourts: Int?,

    @SerializedName("sportTypes")
    val sportTypes: List<String>?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("distance")
    val distance: Double?,

    @SerializedName("ownerId")
    val ownerId: Long?,

    @SerializedName("ownerName")
    val ownerName: String?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?
)
