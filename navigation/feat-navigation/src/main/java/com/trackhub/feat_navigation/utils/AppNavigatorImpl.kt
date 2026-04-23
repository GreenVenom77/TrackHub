package com.trackhub.feat_navigation.utils

import androidx.navigation.NavHostController
import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.utils.AppNavigator
import kotlin.reflect.KClass

class AppNavigatorImpl: AppNavigator {
    private lateinit var returnedDestination: KClass<out Destination>
    override lateinit var navController: NavHostController
        private set

    override fun config(
        returnedDestination: KClass<out Destination>,
        navController: NavHostController
    ) {
        this.returnedDestination = returnedDestination
        this.navController = navController
    }

    override fun navigateTo(target: Destination) {
        navController.navigate(target) {
            launchSingleTop = true
        }
    }

    override fun navigateBack() {
        navController.navigateUp()
    }

    override fun navigateAndClearBackStack(target: Destination) {
        navController.navigate(target) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    override fun navigateFromBottomBar(target: Destination) {
        navController.navigate(target) {
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun getCurrentDestination(): Destination? {
        return navController.currentBackStackEntry?.destination
            ?.toNavigationTarget<Destination>(returnedDestination)
    }

    override fun getPreviousDestination(): Destination? {
        return navController.previousBackStackEntry?.destination
            ?.toNavigationTarget<Destination>(returnedDestination)
    }
}
