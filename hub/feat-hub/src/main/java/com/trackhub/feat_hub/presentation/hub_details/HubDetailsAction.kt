package com.trackhub.feat_hub.presentation.hub_details

import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item

interface HubDetailsAction {
    data class UpdateHub(
        val updatedHub: Hub
    ): HubDetailsAction
    data class DeleteHub(val hubId: String): HubDetailsAction
    data class AddItem(
        val newItem: Item
    ): HubDetailsAction
    data class UpdateItem(
        val updatedItem: Item,
    ): HubDetailsAction
    data class DeleteItem(val itemId: Int): HubDetailsAction
    data class ChangeCurrentItem(val item: Item?): HubDetailsAction
    data object ClearNetworkOperations: HubDetailsAction
    data object NavigateBack: HubDetailsAction
}