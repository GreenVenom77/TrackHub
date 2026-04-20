package com.trackhub.feat_menu.presentation

interface MenuAction {
    data object NavigateToProfile: MenuAction
    data class ChangeLanguage(val languageTag: String): MenuAction
    data class ChangeTheme(val isDarkTheme: Boolean): MenuAction
    data object Logout: MenuAction
}