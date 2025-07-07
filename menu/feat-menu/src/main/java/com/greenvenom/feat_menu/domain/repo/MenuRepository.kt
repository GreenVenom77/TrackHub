package com.greenvenom.feat_menu.domain.repo

import android.content.Context
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult

interface MenuRepository {
    fun isCurrentThemeDark(): Boolean?

    fun isCurrentLanguageArabic(): Boolean

    suspend fun changeTheme(isDarkTheme: Boolean)

    fun changeLanguage(languageTag: String)

    suspend fun logout(): NetworkResult<Unit, NetworkError>
}