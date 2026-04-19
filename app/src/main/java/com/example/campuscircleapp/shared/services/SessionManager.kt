package com.example.campuscircleapp.shared.services

import android.content.Context

object SessionManager {
    private const val PREF_NAME = "campus_circle_pref"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "user_role"
    private const val KEY_NAME = "user_name"

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

    fun clearToken(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .remove(KEY_ROLE)
            .remove(KEY_NAME)
            .apply()
    }
}
