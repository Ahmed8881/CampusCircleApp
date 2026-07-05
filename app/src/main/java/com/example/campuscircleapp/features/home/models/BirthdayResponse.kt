package com.example.campuscircleapp.features.home.models

data class BirthdayResponse(
    val id: Long,
    val name: String,
    val avatar: String?,
    val birthdayDay: Int?,
    val birthdayMonth: String?
)
