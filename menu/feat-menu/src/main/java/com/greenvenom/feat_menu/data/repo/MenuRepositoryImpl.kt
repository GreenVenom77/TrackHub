package com.greenvenom.feat_menu.data.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.feat_menu.domain.remote.MenuRemoteDataSource
import com.greenvenom.feat_menu.domain.repo.MenuRepository
import com.seravian.core_local.domain.AppPrefsDataSource

class MenuRepositoryImpl(
    private val remoteDataSource: MenuRemoteDataSource,
    private val appPrefStateRepository: AppPrefsDataSource
): MenuRepository {
    override fun isCurrentThemeDark(): Boolean? {
        return appPrefStateRepository.appPrefsState.value.isDarkTheme
    }

    override fun isCurrentLanguageArabic(): Boolean {
        return appPrefStateRepository.appPrefsState.value.currentLanguageTag == "ar"
    }

    override suspend fun changeTheme(isDarkTheme: Boolean) {
        appPrefStateRepository.changeTheme(
            isDarkTheme = isDarkTheme
        )
    }

    override fun changeLanguage(languageTag: String) {
        appPrefStateRepository.changeLanguage(languageTag)
    }

    override suspend fun logout(): EmptyResult<NetworkError> {
        return remoteDataSource.logoutUser()
    }
}