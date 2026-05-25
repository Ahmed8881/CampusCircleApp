package com.example.campuscircleapp.features.auth.models

data class UserDeviceTokenDTO(
    val deviceToken: String,
    val fcmToken: String,
    val platform: String,
    val deviceName: String
)