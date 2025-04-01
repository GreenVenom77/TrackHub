package com.trackhub.feat_network.data.features.menu

import android.content.Context
import com.greenvenom.core_menu.data.AppPrefStateRepository
import com.greenvenom.core_menu.domain.MenuRepository
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.trackhub.feat_network.domain.remote.RemoteDataSource
import kotlinx.coroutines.flow.first

class MenuRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val appPrefStateRepository: AppPrefStateRepository
): MenuRepository {
    override fun isCurrentThemeDark(): Boolean {
        return appPrefStateRepository.appPrefState.value.isDarkTheme
    }

    override fun isCurrentLanguageArabic(): Boolean {
        return appPrefStateRepository.appPrefState.value.currentLanguageTag == "ar"
    }

    override suspend fun changeTheme(context: Context, isDarkTheme: Boolean) {
        appPrefStateRepository.changeTheme(
            context = context,
            isDarkTheme = isDarkTheme
        )
    }

    override fun changeLanguage(languageTag: String) {
        appPrefStateRepository.changeLanguage(languageTag)
    }

    override suspend fun logout(): NetworkResult<Unit, NetworkError> {
        val result = remoteDataSource.logoutUser()
        return result.map {  }
    }
}