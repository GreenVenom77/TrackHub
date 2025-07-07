package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.greenvenom.core_navigation.data.NavigationType
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsScreen
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun NavGraphBuilder.ownedHubsGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.OwnedHubs>(startDestination = Screen.MyHubs) {
        composable<Screen.MyHubs> {
            HubListScreen(
                showOwnedHubs = true,
                navigateToHubDetails = { hubId ->
                    navigate(
                        NavigationType.Standard(Screen.MyHubDetails(hubId))
                    )
                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }

        composable<Screen.MyHubDetails> {
            val args = it.toRoute<Screen.MyHubDetails>()

            HubDetailsScreen(
                hubId = args.hubId,
                navigateBack = {
                    navigate(NavigationType.Back)
                }
            )
        }
    }
}