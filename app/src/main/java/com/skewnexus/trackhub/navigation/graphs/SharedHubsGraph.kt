package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun NavGraphBuilder.sharedHubsGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.SharedHubs>(startDestination = Screen.SharedHubs) {
        composable<Screen.SharedHubs> {
            HubListScreen(
                showOwnedHubs = false,
                navigateToHubDetails = { hubId ->
                    navigate(
                        NavigationType.Standard(Screen.MyHubDetails(hubId))
                    )
                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }
    }
}