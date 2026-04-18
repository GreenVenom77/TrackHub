package com.trackhub.feat_hub.presentation.hub_list

interface HubListAction {
    data class AddHub(val hubName: String, val hubDescription: String) : HubListAction
    data class NavigateToHubDetails(val hubId: String) : HubListAction
    data object Refresh: HubListAction
    data object ClearNetworkOperations: HubListAction
}