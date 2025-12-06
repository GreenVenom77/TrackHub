package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.greenvenom.feat_menu.presentation.MenuScreen
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph
import com.trackhub.feat_notifications.presentation.screens.NotificationsScreen

fun NavGraphBuilder.mainGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.Main>(startDestination = Screen.OwnedHubs) {
        composable<Screen.OwnedHubs> {
            HubListScreen(
                areHubsOwned = true,
                navigateToHubDetails = { hubId ->
                    navigate(
                        NavigationType.Standard(SubGraph.HubDetails(hubId))
                    )
                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }

        composable<Screen.SharedHubs> {
            HubListScreen(
                areHubsOwned = false,
                navigateToHubDetails = { hubId ->
                    navigate(
                        NavigationType.Standard(SubGraph.HubDetails(hubId))
                    )
                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }

        composable<Screen.Notifications> {
            NotificationsScreen(
                navigateBack = { navigate(NavigationType.Back) }
            )
        }

        composable<Screen.Menu> {
            MenuScreen(
                navigateToProfile = {

                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }
    }
}