package com.greenvenom.core_navigation.utils

import androidx.navigation.NavHostController
import com.greenvenom.core_navigation.routes.Destination
import kotlin.reflect.KClass

interface AppNavigator {
    val navController: NavHostController

    fun config(
        returnedDestination: KClass<out Destination>,
        navController: NavHostController
    )

    fun navigateTo(target: Destination)

    fun navigateBack()

    fun navigateAndClearBackStack(target: Destination)

    fun navigateFromBottomBar(target: Destination)

    fun getCurrentDestination(): Destination?

    fun getPreviousDestination(): Destination?
}