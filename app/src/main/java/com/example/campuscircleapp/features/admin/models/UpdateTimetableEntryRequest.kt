package com.example.campuscircleapp.features.admin.models

data class UpdateTimetableEntryRequest(
    val id: Long,
    val courseId: Long,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val spaceId: Long
)
