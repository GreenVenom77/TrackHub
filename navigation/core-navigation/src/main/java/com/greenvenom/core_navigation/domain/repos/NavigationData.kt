package com.greenvenom.core_navigation.domain.repos

import androidx.compose.runtime.Immutable
import com.greenvenom.core_navigation.routes.Destination

/**
 * Data class representing the state of navigation UI.
 * Holds current and previous destinations, flags for side/bottom/top bar visibility, and account type index.
 */
@Immutable
data class NavigationData(
    val currentDestination: Destination? = null,
    val previousDestination: Destination? = null,
    val isCurrentDestinationSide: Boolean = false,
    val bottomBarState: Boolean = false,
    val topBarState: Boolean = false,
    val accountTypeIndex: Int = 0
)