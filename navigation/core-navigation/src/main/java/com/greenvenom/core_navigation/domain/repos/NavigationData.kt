package com.greenvenom.core_navigation.domain.repos

import androidx.compose.runtime.Immutable
import com.greenvenom.core_navigation.routes.Destination

@Immutable
data class NavigationData(
    val currentDestination: Destination? = null,
    val previousDestination: Destination? = null,
    val isCurrentDestinationSide: Boolean = false,
    val bottomBarState: Boolean = false,
    val topBarState: Boolean = false,
    val accountTypeIndex: Int = 0
)