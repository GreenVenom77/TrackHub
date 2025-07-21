package com.trackhub.feat_hub.presentation.hub_details

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem

data class HubDetailsState(
    val hub: Hub? = null,
    val hubItems: List<HubItem> = emptyList(),
    val currentItem: HubItem? = null,
    val hubDeletionResult: EmptyResult<NetworkError>? = null,
    val hubUpdateResult: EmptyResult<NetworkError>? = null,
    val operationResult: EmptyResult<NetworkError>? = null,
    val itemDeletionResult: EmptyResult<NetworkError>? = null,
    val hubItemsResult: EmptyResult<NetworkError>? = null
)