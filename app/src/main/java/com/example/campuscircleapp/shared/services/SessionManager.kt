package com.example.campuscircleapp.shared.services

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "campus_circle_pref"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "user_role"
    private const val KEY_NAME = "user_name"
    private const val KEY_FCM_TOKEN = "fcm_token"
    private const val KEY_DEVICE_TOKEN = "device_token"
    private const val KEY_DEVICE_NAME = "device_name"
    private const val KEY_PLATFORM = "platform"
    private const val KEY_PERMISSION_GRANTED = "notification_permission_granted"
    private const val KEY_LAST_REGISTERED_FCM_TOKEN = "last_registered_fcm_token"

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun saveRole(context: Context, role: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun saveName(context: Context, name: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAME, name)
            .apply()
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    fun getRole(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, null)
    }

    fun getName(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME, null)
    }

    fun saveDeviceInfo(context: Context, fcmToken: String, deviceToken: String, deviceName: String, platform: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FCM_TOKEN, fcmToken)
            .putString(KEY_DEVICE_TOKEN, deviceToken)
            .putString(KEY_DEVICE_NAME, deviceName)
            .putString(KEY_PLATFORM, platform)
            .apply()
    }

    fun getFcmToken(context: Context): String? = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_FCM_TOKEN, null)
    fun getDeviceToken(context: Context): String? = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_DEVICE_TOKEN, null)
    fun getDeviceName(context: Context): String? = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_DEVICE_NAME, null)
    fun getPlatform(context: Context): String? = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_PLATFORM, null)

    fun setNotificationPermissionGranted(context: Context, granted: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_PERMISSION_GRANTED, granted)
            .apply()
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PERMISSION_GRANTED, false)
    }

    fun setLastRegisteredFcmToken(context: Context, token: String?) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_REGISTERED_FCM_TOKEN, token)
            .apply()
    }

    fun getLastRegisteredFcmToken(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_REGISTERED_FCM_TOKEN, null)
    }

    fun clearToken(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .remove(KEY_ROLE)
            .remove(KEY_NAME)
            .remove(KEY_LAST_REGISTERED_FCM_TOKEN) // Reset on logout
            .apply()
    }
}
