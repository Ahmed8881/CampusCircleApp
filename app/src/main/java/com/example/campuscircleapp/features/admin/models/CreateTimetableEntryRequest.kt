package com.example.campuscircleapp.features.admin.models

data class CreateTimetableEntryRequest(
    val courseId: Long,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val spaceId: Long
)
