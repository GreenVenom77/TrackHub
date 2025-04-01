package com.greenvenom.feat_menu.presentation

import android.content.Context

interface MenuAction {
    data object NavigateToProfile: MenuAction
    data class ChangeLanguage(val languageTag: String): MenuAction
    data class ChangeTheme(val context: Context): MenuAction
    data object Logout: MenuAction
}