package com.trackhub.feat_hub.presentation.hub_details

import com.trackhub.core_hub.domain.models.HubItem

interface HubDetailsAction {
    data class UpdateHub(
        val hubName: String = "",
        val hubDescription: String = ""
    ): HubDetailsAction
    data class DeleteHub(val hubId: String): HubDetailsAction
    data class AddItem(
        val itemName: String,
        val itemStock: Float,
        val itemUnit: String
    ): HubDetailsAction
    data class UpdateItem(
        val itemId: Int,
        val itemName: String = "",
        val itemStock: Float = -1f,
        val itemUnit: String = ""
    ): HubDetailsAction
    data class DeleteItem(val itemId: Int): HubDetailsAction
    data class UpdateCurrentItem(val hubItem: HubItem?): HubDetailsAction
    data object ClearNetworkOperations: HubDetailsAction
    data object NavigateBack: HubDetailsAction
}