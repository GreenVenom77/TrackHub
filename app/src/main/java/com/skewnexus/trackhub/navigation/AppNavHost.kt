package com.skewnexus.trackhub.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_navigation.utils.AppNavigator
import com.greenvenom.feat_auth.presentation.splash.SplashScreen
import com.skewnexus.trackhub.navigation.graphs.authGraph
import com.skewnexus.trackhub.navigation.graphs.menuGraph
import com.skewnexus.trackhub.navigation.graphs.notificationsGraph
import com.skewnexus.trackhub.navigation.graphs.ownedHubsGraph
import com.skewnexus.trackhub.navigation.graphs.sharedHubsGraph
import com.skewnexus.trackhub.navigation.utils.SessionDestinationHandler
import com.trackhub.feat_navigation.routes.Screen
import org.koin.compose.koinInject

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val appNavigator = koinInject<AppNavigator>()
    val navigationRepository = koinInject<NavigationStateRepository>()
    val destinationHandler = koinInject<SessionDestinationHandler>()

    appNavigator.config(
        returnedDestination = Screen::class,
        navController = rememberNavController()
    )

    NavHost(
        navController = appNavigator.navController,
        startDestination = Screen.Splash,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onStart = {
                    destinationHandler.collectSessionDestinations()
                }
            )
        }

        authGraph(
            navigate = navigationRepository::navigate,
            navigationStateRepository = navigationRepository
        )

        ownedHubsGraph(
            navigate = navigationRepository::navigate
        )

        sharedHubsGraph(
            navigate = navigationRepository::navigate
        )

        notificationsGraph(
            navigate = navigationRepository::navigate
        )

        menuGraph(
            navigate = navigationRepository::navigate
        )
    }
}