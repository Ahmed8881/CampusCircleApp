package com.example.campuscircleapp.features.notifications.models

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("message") val body: String?, // Mapped from 'message' in JSON
    @SerializedName("type") val type: String?,
    @SerializedName("isRead") var isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("spaceId") val spaceId: Long?
)
