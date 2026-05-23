package com.example.campuscircleapp.features.home.models

import com.google.gson.annotations.SerializedName

data class PendingEnrollmentResponse(
    @SerializedName("enrollmentId") val enrollmentId: Long,
    @SerializedName("studentName") val studentName: String,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("courseCode") val courseCode: String,
    @SerializedName("requestedAt") val requestedAt: String?
)
