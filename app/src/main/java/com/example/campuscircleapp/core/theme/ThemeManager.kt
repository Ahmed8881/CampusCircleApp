package com.example.campuscircleapp.core.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.campuscircleapp.R

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"
    private const val KEY_MODE = "dark_mode"
    private const val DEFAULT_THEME = "seckho"

    private const val ONBOARDING_PREFS = "onboarding_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    val availableThemes: List<ThemeInfo> = listOf(
        ThemeInfo("seckho", "Seekho Editorial", "Warm cream & olive", R.style.Theme_CampusCircleApp_SeckhoLight, R.style.Theme_CampusCircleApp_SeckhoDark),
        ThemeInfo("ocean", "Ocean Blue", "Cool blues & teal", R.style.Theme_CampusCircleApp_OceanLight, R.style.Theme_CampusCircleApp_OceanDark),
        ThemeInfo("midnight", "Midnight", "Indigo dusk", R.style.Theme_CampusCircleApp_MidnightLight, R.style.Theme_CampusCircleApp_MidnightDark),
        ThemeInfo("forest", "Forest", "Earthy greens", R.style.Theme_CampusCircleApp_ForestLight, R.style.Theme_CampusCircleApp_ForestDark),
        ThemeInfo("lavender", "Lavender", "Soft purples", R.style.Theme_CampusCircleApp_LavenderLight, R.style.Theme_CampusCircleApp_LavenderDark),
        ThemeInfo("sunset", "Sunset", "Warm oranges", R.style.Theme_CampusCircleApp_SunsetLight, R.style.Theme_CampusCircleApp_SunsetDark),
        ThemeInfo("rose", "Rose", "Pink & rose gold", R.style.Theme_CampusCircleApp_RoseLight, R.style.Theme_CampusCircleApp_RoseDark),
        ThemeInfo("slate", "Slate", "Cool gray", R.style.Theme_CampusCircleApp_SlateLight, R.style.Theme_CampusCircleApp_SlateDark),
        ThemeInfo("aubergine", "Aubergine", "Deep eggplant", R.style.Theme_CampusCircleApp_AubergineLight, R.style.Theme_CampusCircleApp_AubergineDark),
        ThemeInfo("gold", "Gold", "Warm amber", R.style.Theme_CampusCircleApp_GoldLight, R.style.Theme_CampusCircleApp_GoldDark),
    )

    private val themeMap = availableThemes.associateBy { it.id }

    fun getThemeStyleRes(themeId: String, isDark: Boolean): Int {
        val info = themeMap[themeId] ?: themeMap[DEFAULT_THEME]!!
        return if (isDark) info.darkStyleRes else info.lightStyleRes
    }

    fun getSavedThemeId(context: Context): String {
        return getPrefs(context).getString(KEY_THEME, DEFAULT_THEME) ?: DEFAULT_THEME
    }

    fun isDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_MODE, false)
    }

    fun saveTheme(context: Context, themeId: String) {
        getPrefs(context).edit().putString(KEY_THEME, themeId).apply()
    }

    fun saveDarkMode(context: Context, dark: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_MODE, dark).apply()
    }

    fun toggleDarkMode(context: Context): Boolean {
        val newMode = !isDarkMode(context)
        saveDarkMode(context, newMode)
        return newMode
    }

    fun setThemeAndRecreate(activity: Activity, themeId: String, dark: Boolean? = null) {
        saveTheme(activity, themeId)
        if (dark != null) saveDarkMode(activity, dark)
        activity.recreate()
    }

    fun toggleDarkModeAndRecreate(activity: Activity) {
        toggleDarkMode(activity)
        activity.recreate()
    }

    fun isOnboardingCompleted(context: Context): Boolean {
        return context.getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(context: Context) {
        context.getSharedPreferences(ONBOARDING_PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    fun applyTheme(activity: Activity) {
        val themeId = getSavedThemeId(activity)
        val isDark = isDarkMode(activity)
        val styleRes = getThemeStyleRes(themeId, isDark)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        activity.setTheme(styleRes)
    }

    fun resolveColor(activity: Activity, attrRes: Int): Int {
        val typedArray = activity.obtainStyledAttributes(intArrayOf(attrRes))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    fun resolveColorCompat(context: Context, attrRes: Int): Int {
        val typedArray = context.obtainStyledAttributes(intArrayOf(attrRes))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}

data class ThemeInfo(
    val id: String,
    val name: String,
    val description: String,
    val lightStyleRes: Int,
    val darkStyleRes: Int
)
