package com.example.campuscircleapp.features.home.models

data class BirthdayResponse(
    val userId: Long,
    val name: String,
    val dob: String,
    val image: String?
)
