package com.trackhub.feat_navigation.utils

import com.greenvenom.core_navigation.domain.NavigationState
import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.utils.AppNavigator

class AppNavigatorImpl: AppNavigator {
    private lateinit var navigationState: NavigationState

    override fun bind(
        navigationState: NavigationState
    ) {
        this.navigationState = navigationState
    }

    override fun navigateTo(target: Destination) {
        navigate(target)
    }

    override fun navigateBack() {
        goBack()
    }

    override fun navigateAndClearBackStack(target: Destination) {
        navigationState.backStacks.forEach { (_, stack) ->
            while (stack.size > 1) stack.removeLastOrNull()
        }
        navigationState.mainRoute = target
        navigationState.topLevelRoute = target
    }

    override fun navigateFromBottomBar(target: Destination) {
        navigate(target)
    }

    override fun getCurrentDestination(): Destination {
        return navigationState.currentRoute as Destination
    }

    override fun getPreviousDestination(): Destination? {
        return navigationState.previousRoute as Destination?
    }

    private fun navigate(destination: Destination) {
        if (destination in navigationState.backStacks.keys) {
            // This is a top level route, just switch to it.
            navigationState.topLevelRoute = destination
        } else {
            navigationState.currentStack.add(destination)
        }
    }

    private fun goBack() {
        when {
            // There are screens to pop in the current stack
            navigationState.currentStack.size > 1 -> {
                navigationState.currentStack.removeLastOrNull()
            }
            // We're at the root of a non-main top-level route, go back to main
            navigationState.topLevelRoute != navigationState.mainRoute -> {
                navigationState.topLevelRoute = navigationState.mainRoute
            }
            // We're at the root of the main route, exit the app
            else -> {
                // Handled Automatically by Navigation 3
            }
        }
    }
}
