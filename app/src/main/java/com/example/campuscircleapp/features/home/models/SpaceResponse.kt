package com.example.campuscircleapp.features.home.models

import com.google.gson.annotations.SerializedName

data class SpaceResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("totalUsers") val totalUsers: Int = 0,
    @SerializedName("status") val status: String? = null,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("updatedBy") val updatedBy: String? = null
)
