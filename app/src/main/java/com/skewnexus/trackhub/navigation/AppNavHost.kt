package com.skewnexus.trackhub.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.greenvenom.core_navigation.domain.rememberNavigationState
import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.greenvenom.core_navigation.domain.toEntries
import com.greenvenom.core_navigation.utils.NavigationType
import com.greenvenom.feat_auth.presentation.routes.AuthDest
import com.greenvenom.feat_auth.presentation.splash.SplashScreen
import com.skewnexus.trackhub.navigation.components.BottomDestination
import com.skewnexus.trackhub.navigation.graphs.authGraph
import com.skewnexus.trackhub.navigation.graphs.menuGraph
import com.skewnexus.trackhub.navigation.graphs.notificationsGraph
import com.skewnexus.trackhub.navigation.graphs.ownedHubsGraph
import com.skewnexus.trackhub.navigation.graphs.sharedHubsGraph
import com.skewnexus.trackhub.navigation.utils.SessionDestinationHandler
import org.koin.compose.koinInject

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navigationRepository = koinInject<NavigationRepository>()
    val destinationHandler = koinInject<SessionDestinationHandler>()
    val splashComplete by destinationHandler.isReady.collectAsStateWithLifecycle()

    val navigationState = rememberNavigationState(
        startRoute = AuthDest.Login,
        topLevelRoutes = buildSet {
            add(AuthDest.Login)
            addAll(BottomDestination.entries.map { it.destination })
        }
    )

    LaunchedEffect(Unit) {
        navigationRepository.bind(
            navigationState = navigationState
        )
    }

    AnimatedContent(
        targetState = splashComplete,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith
                    fadeOut(animationSpec = tween(500))
        },
        modifier = Modifier.fillMaxSize()
    ) { isSplashComplete ->
        if (!isSplashComplete) {
            SplashScreen(
                onStart = { destinationHandler.collectSessionDestinations() }
            )
        } else {
            NavDisplay(
                entries = navigationState.toEntries(
                    entryProvider = entryProvider {
                        authGraph(navigationRepository::navigate)
                        ownedHubsGraph(navigationRepository::navigate)
                        sharedHubsGraph(navigationRepository::navigate)
                        notificationsGraph(navigationRepository::navigate)
                        menuGraph(navigationRepository::navigate)
                    }
                ),
                onBack = { navigationRepository.navigate(NavigationType.Back) },
                modifier = modifier
            )
        }
    }
}