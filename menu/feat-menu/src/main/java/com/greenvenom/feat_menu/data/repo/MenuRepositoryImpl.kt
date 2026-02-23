package com.greenvenom.feat_menu.data.repo

import com.greenvenom.core_menu.data.mappers.toDomain
import com.greenvenom.core_menu.domain.Profile
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.feat_menu.domain.cache.MenuCacheDataSource
import com.greenvenom.feat_menu.domain.remote.MenuRemoteDataSource
import com.greenvenom.feat_menu.domain.repo.MenuRepository
import com.seravian.core_local.domain.AppPrefsDataSource

class MenuRepositoryImpl(
    private val cacheDataSource: MenuCacheDataSource,
    private val remoteDataSource: MenuRemoteDataSource,
    private val appPrefStateRepository: AppPrefsDataSource
): MenuRepository {
    override suspend fun getProfile(): Profile {
        return cacheDataSource.getProfile()?.toDomain() as Profile
    }

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
        return remoteDataSource.logoutUser().onSuccess {
            cacheDataSource.deleteProfile()
        }
    }
}