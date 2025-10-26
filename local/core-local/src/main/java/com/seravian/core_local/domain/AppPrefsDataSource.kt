package com.seravian.core_local.domain

import com.seravian.core_local.data.AppPrefsState
import kotlinx.coroutines.flow.StateFlow

interface AppPrefsDataSource {
    val appPrefsState: StateFlow<AppPrefsState>

    suspend fun changeTheme(isDarkTheme: Boolean)

    suspend fun getThemePreference()

    fun changeLanguage(languageTag: String)

    fun getCurrentLanguage(): String
}