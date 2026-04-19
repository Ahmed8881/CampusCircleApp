package com.example.campuscircleapp.features.auth.models

import com.google.gson.annotations.SerializedName

data class GoogleAuthRequest(
    @SerializedName("Provider") val provider: String = "Google",
    @SerializedName("GoogleToken") val googleToken: String,
    @SerializedName("GoogleId") val googleId: String,
    @SerializedName("Email") val email: String,
    @SerializedName("FullName") val fullName: String
)