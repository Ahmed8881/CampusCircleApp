package com.example.campuscircleapp.features.home.models

import com.google.gson.annotations.SerializedName

data class AnnouncementItem(
    @SerializedName("id") val id: Long,
    @SerializedName("message") val message: String,
    @SerializedName("spaceName") val spaceName: String?,
    @SerializedName("sentAt") val sentAt: String?,
    @SerializedName("sentBy") val sentBy: String?
)
