package com.example.campuscircleapp.features.home.models

data class AdminCourseResponse(
    val id: Long,
    val name: String,
    val description: String,
    val semester: String,
    val semesterNo: Int,
    val code: String,
    val credits: Int,
    val startDate: String?,
    val endDate: String?,
    val isActive: Boolean,
    val department: String,
    val instructor: String
)
