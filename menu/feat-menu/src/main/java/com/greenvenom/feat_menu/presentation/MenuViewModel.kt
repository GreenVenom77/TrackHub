package com.greenvenom.feat_menu.presentation

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.greenvenom.core_menu.domain.MenuRepository
import com.greenvenom.core_ui.presentation.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val menuRepository: MenuRepository,
): BaseViewModel() {
    private val _menuState = MutableStateFlow(MenuState())
    val menuState = _menuState.asStateFlow()

    init {
        _menuState.update {
            it.copy(
                isArabic = menuRepository.isCurrentLanguageArabic(),
                isDarkTheme = menuRepository.isCurrentThemeDark()
            )
        }
    }

    fun menuAction(action: MenuAction) {
        when (action) {
            is MenuAction.ChangeTheme -> changeTheme(action.context)
            is MenuAction.ChangeLanguage -> changeLanguage(action.languageTag)
            is MenuAction.Logout -> logout()
        }
    }

    private fun changeTheme(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.changeTheme(
                context = context,
                isDarkTheme = !_menuState.value.isDarkTheme
            )
            _menuState.update {
                it.copy(
                    isDarkTheme = !it.isDarkTheme
                )
            }
        }
    }

    private fun changeLanguage(languageTag: String) {
        menuRepository.changeLanguage(languageTag)
        _menuState.update {
            it.copy(
                isArabic = languageTag == "ar"
            )
        }
    }

    private fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = menuRepository.logout()

            _menuState.update {
                it.copy(
                    logoutResult = result
                )
            }
        }
    }
}