package com.example.campuscircleapp.features.admin.models

import com.google.gson.annotations.SerializedName

data class SemesterResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("number") val number: Int,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("updatedBy") val updatedBy: String? = null
)
