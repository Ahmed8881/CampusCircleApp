package com.example.campuscircleapp.features.admin.models

data class CreateInstructorRequest(
    val fullName: String,
    val userName: String,
    val email: String,
    val password: String,
    val dob: String
)
