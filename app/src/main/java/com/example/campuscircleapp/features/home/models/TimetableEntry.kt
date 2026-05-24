package com.example.campuscircleapp.features.home.models

import com.google.gson.annotations.SerializedName

data class TimetableEntry(
    @SerializedName("id") val id: Long,
    @SerializedName("day") val day: Int,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("room") val room: String,
    @SerializedName("courseId") val courseId: Long,
    @SerializedName("courseName") val courseName: String,
    @SerializedName("courseCode") val courseCode: String
)
