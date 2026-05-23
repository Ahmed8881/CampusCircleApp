package com.example.campuscircleapp.features.teacher.models

data class AnnounceAssessmentRequest(
    val type: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val totalMarks: Int
)
