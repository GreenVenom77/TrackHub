package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsScreen
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun EntryProviderScope<NavKey>.sharedHubsGraph(
    navigate: (NavigationType) -> Unit
) {
    entry<Screen.SharedHubs> { key ->
        HubListScreen(
            areHubsOwned = key.ownedHubs,
            navigateToHubDetails = { hubId ->
                navigate(
                    NavigationType.Standard(SubGraph.HubDetails(hubId))
                )
            },
            navigateBack = { navigate(NavigationType.Back) },
        )
    }

    entry<Screen.SharedHubDetails> { key ->
        HubDetailsScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            }
        )
    }
}