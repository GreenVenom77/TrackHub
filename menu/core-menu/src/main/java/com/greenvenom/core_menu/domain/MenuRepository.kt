package com.greenvenom.core_menu.domain

import android.content.Context
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult

interface MenuRepository {
    fun isCurrentThemeDark(): Boolean
    fun isCurrentLanguageArabic(): Boolean
    suspend fun changeTheme(context: Context, isDarkTheme: Boolean)
    fun changeLanguage(languageTag: String)
    suspend fun logout(): NetworkResult<Unit, NetworkError>
}