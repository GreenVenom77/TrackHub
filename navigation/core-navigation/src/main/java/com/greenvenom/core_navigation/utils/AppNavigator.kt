package com.greenvenom.core_navigation.utils

import com.greenvenom.core_navigation.domain.NavigationState
import com.greenvenom.core_navigation.routes.Destination

interface AppNavigator {
    fun bind(
        navigationState: NavigationState
    )

    fun navigateTo(target: Destination)

    fun navigateBack()

    fun navigateAndClearBackStack(target: Destination)

    fun navigateFromBottomBar(target: Destination)

    fun getCurrentDestination(): Destination

    fun getPreviousDestination(): Destination?
}