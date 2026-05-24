package com.example.campuscircleapp.features.home.models

data class ResetPasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val token: String
)
