package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun NavGraphBuilder.hubDetailsGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.HubDetails>(startDestination = Screen.HubDetails) {
        composable<Screen.HubDetails> {
            HubDetailsScreen(
                navigateBack = {
                    navigate(NavigationType.Back)
                }
            )
        }
    }
}