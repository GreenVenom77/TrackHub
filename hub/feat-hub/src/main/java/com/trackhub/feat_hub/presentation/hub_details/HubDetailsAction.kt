package com.trackhub.feat_hub.presentation.hub_details

import com.trackhub.core_hub.domain.models.Item

interface HubDetailsAction {
    data class UpdateHub(
        val hubName: String,
        val hubDescription: String
    ): HubDetailsAction
    data class DeleteHub(val hubId: String): HubDetailsAction
    data class AddItem(
        val itemName: String,
        val itemStock: Float,
        val itemUnit: String,
        val manufacturer: String? = null,
        val category: String? = null
    ): HubDetailsAction
    data class UpdateItem(
        val itemName: String,
        val itemStock: Float,
        val itemUnit: String,
        val manufacturer: String? = null,
        val category: String? = null
    ): HubDetailsAction
    data class DeleteItem(val itemId: Int): HubDetailsAction
    data class ChangeCurrentItem(val item: Item?): HubDetailsAction
    data object ClearNetworkOperations: HubDetailsAction
    data object NavigateBack: HubDetailsAction
}