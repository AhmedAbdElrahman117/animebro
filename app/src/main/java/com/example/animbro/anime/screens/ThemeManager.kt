package com.example.animbro.anime.screens
import android.content.Context
import androidx.compose.runtime.Composable

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

}