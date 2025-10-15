package com.trackhub.feat_hub.presentation.hub_details

import androidx.paging.PagingData
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class HubDetailsState(
    val hub: Hub? = null,
    val items: Flow<PagingData<Item>> = flow { emit(PagingData.from(emptyList())) },
    val currentItem: Item? = null,
    val selectedCategory: String? = null,
    val selectedManufacturer: String? = null,
    val hubDeletionResult: EmptyResult<NetworkError>? = null,
    val hubUpdateResult: EmptyResult<NetworkError>? = null,
    val operationResult: EmptyResult<NetworkError>? = null,
    val itemDeletionResult: EmptyResult<NetworkError>? = null,
    val hubItemsResult: EmptyResult<NetworkError>? = null
)