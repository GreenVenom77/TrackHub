package com.greenvenom.feat_menu.presentation

import androidx.compose.runtime.Immutable
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult

@Immutable
data class MenuState(
    val isArabic: Boolean = false,
    val isDarkTheme: Boolean = false,
    val logoutResult: NetworkResult<Unit, NetworkError>? = null
)
