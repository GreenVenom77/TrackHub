package com.greenvenom.core_util.theme

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.greenvenom.core_util.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "clutch_theme_prefs")

/**
 * Manages application theme preferences with system theme support.
 * Uses DataStore for modern, type-safe storage.
 */
class ThemeManager(
    private val context: Context
) {
    companion object {
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    /**
     * Flow that emits the current theme mode setting
     */
    val themeModeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: THEME_SYSTEM
        }

    /**
     * Flow that emits whether dark theme should be active
     * Takes system preference into account when in THEME_SYSTEM mode
     */
    val isDarkThemeFlow: Flow<Boolean> = themeModeFlow.map { mode ->
        Logger.d("Theme mode: $mode", "isDarkThemeFlow")
        isDarkThemeForMode(mode)
    }

    suspend fun isDarkTheme(): Boolean {
        return themeModeFlow.map { mode ->
            Logger.d("Theme mode: $mode", "isDarkTheme")
            isDarkThemeForMode(mode)
        }.first()
    }

    /**
     * Set the theme mode (system, light, or dark)
     */
    suspend fun setThemeMode(mode: String) {
        require(mode in listOf(THEME_SYSTEM, THEME_LIGHT, THEME_DARK)) {
            "Invalid theme mode: $mode"
        }
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    /**
     * Convenience method to set dark or light theme directly
     */
    suspend fun setDarkMode(isDark: Boolean) {
        setThemeMode(if (isDark) THEME_DARK else THEME_LIGHT)
    }

    /**
     * Toggle between light and dark mode
     * Note: This exits system theme mode
     */
    suspend fun toggleTheme() {
        val currentMode = getCurrentThemeMode()
        Logger.d("Theme mode: $currentMode", "toggleTheme")
        val isDark = isDarkThemeForMode(currentMode)
        Logger.d("Theme mode: $isDark", "toggleTheme")
        setDarkMode(!isDark)
    }

    /**
     * Get current theme mode synchronously (for immediate use)
     * Prefer using themeModeFlow for reactive UI
     */
    private suspend fun getCurrentThemeMode(): String {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_MODE_KEY] ?: THEME_SYSTEM
        }.first()
    }

    /**
     * Determine if dark theme should be active for a given mode
     */
    private fun isDarkThemeForMode(mode: String): Boolean {
        return when (mode) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            THEME_SYSTEM -> isSystemInDarkMode()
            else -> false
        }
    }

    /**
     * Check if system is currently in dark mode
     */
    private fun isSystemInDarkMode(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}