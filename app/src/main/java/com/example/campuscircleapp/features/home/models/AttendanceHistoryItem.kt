package com.example.campuscircleapp.features.home.models

data class AttendanceHistoryItem(
    val date: String,
    val status: String,
    val markedBy: String,
    val courseId: Long
)
