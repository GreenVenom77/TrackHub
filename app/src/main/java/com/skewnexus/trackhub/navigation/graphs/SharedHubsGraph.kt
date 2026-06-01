package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsScreen
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.trackhub.feat_hub.presentation.routes.HubDest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

fun EntryProviderScope<NavKey>.sharedHubsGraph(
    navigate: (NavigationType) -> Unit
) {
    entry<HubDest.SharedHubs> { key ->
        HubListScreen(
            navigateToHubDetails = { hubId ->
                navigate(
                    NavigationType.Standard(HubDest.SharedHubDetails(hubId))
                )
            },
            navigateBack = { navigate(NavigationType.Back) },
            hubListViewModel = koinViewModel {
                parametersOf(key.areHubsOwned)
            }
        )
    }

    entry<HubDest.SharedHubDetails> { key ->
        HubDetailsScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            hubDetailsViewModel = koinViewModel {
                parametersOf(key.hubId)
            }
        )
    }
}