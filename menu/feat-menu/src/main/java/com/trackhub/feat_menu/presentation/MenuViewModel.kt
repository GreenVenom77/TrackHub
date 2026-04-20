package com.trackhub.feat_menu.presentation

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.core_util.locale.LocaleManager
import com.greenvenom.core_util.theme.ThemeManager
import com.trackhub.feat_menu.domain.repo.MenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    private val themeManger: ThemeManager,
    private val menuRepository: MenuRepository,
): BaseViewModel() {
    private val _menuState = MutableStateFlow(MenuState())
    val menuState = _menuState.asStateFlow()

    init {
        viewModelScope.launch {
            _menuState.update {
                it.copy(
                    isArabic = LocaleManager.isArabic(),
                    isDarkTheme = withContext(Dispatchers.IO) { themeManger.isDarkTheme() }
                )
            }
        }

        viewModelScope.launch {
            _menuState.update {
                it.copy(
                    profile = withContext(Dispatchers.IO) { menuRepository.getProfile() }
                )
            }
        }
    }

    fun menuAction(action: MenuAction) {
        when (action) {
            is MenuAction.ChangeTheme -> changeTheme(action.isDarkTheme)
            is MenuAction.ChangeLanguage -> changeLanguage(action.languageTag)
            is MenuAction.Logout -> logout()
        }
    }

    private fun changeTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                themeManger.setThemeMode(
                    if (isDarkTheme) ThemeManager.THEME_DARK else ThemeManager.THEME_LIGHT
                )
            }

            _menuState.update {
                it.copy(
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }

    private fun changeLanguage(languageTag: String) {
        LocaleManager.setLocale(languageTag)
        _menuState.update {
            it.copy(
                isArabic = languageTag == "ar"
            )
        }
    }

    private fun logout() {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                menuRepository.logout()
            }

            _menuState.update {
                it.copy(
                    logoutResult = result
                )
            }
            baseAction(BaseAction.HideLoading)
        }
    }
}