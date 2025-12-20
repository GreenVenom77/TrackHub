package com.greenvenom.feat_menu.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError

interface MenuRepository {
    fun isCurrentThemeDark(): Boolean?

    fun isCurrentLanguageArabic(): Boolean

    suspend fun changeTheme(isDarkTheme: Boolean)

    fun changeLanguage(languageTag: String)

    suspend fun logout(): EmptyResult<NetworkError>
}