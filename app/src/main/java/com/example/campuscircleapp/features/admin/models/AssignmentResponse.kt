package com.example.campuscircleapp.features.admin.models

data class AssignmentResponse(
    val assessmentId: Long,
    val title: String,
    val description: String,
    val type: String,
    val dueDate: String,
    val totalMarks: Int,
    val courseName: String?
)
