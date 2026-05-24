package com.example.campuscircleapp.features.admin.models

data class SemesterResponse(
    val semesterId: Long,
    val semesterName: String,
    val semesterNo: Int,
    val startDate: String,
    val endDate: String
)
