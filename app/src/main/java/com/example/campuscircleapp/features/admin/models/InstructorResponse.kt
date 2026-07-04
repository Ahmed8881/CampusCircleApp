package com.example.campuscircleapp.features.admin.models

import com.google.gson.annotations.SerializedName

data class InstructorResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("username") val username: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("updatedBy") val updatedBy: String? = null
)
