package com.greenvenom.feat_menu.presentation

import androidx.compose.runtime.Immutable
import com.greenvenom.core_menu.domain.Profile
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError

@Immutable
data class MenuState(
    val profile: Profile? = null,
    val isArabic: Boolean = false,
    val isDarkTheme: Boolean? = null,
    val logoutResult: EmptyResult<NetworkError>? = null
)
