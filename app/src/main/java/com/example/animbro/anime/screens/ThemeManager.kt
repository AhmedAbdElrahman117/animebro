package com.example.animbro.anime.screens
import android.content.Context
import androidx.compose.runtime.Composable

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ThemeManager(context: Context) {
    private val prefs = context.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode", false)
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    val themeFlow: Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "dark_mode") {
                trySend(sharedPreferences.getBoolean("dark_mode", false))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(isDarkMode()) // Emit initial value
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }
}
