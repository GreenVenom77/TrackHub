package com.trackhub.feat_hub.presentation.hub_details

import androidx.paging.PagingData
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.core_hub.domain.models.UserSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class HubDetailsState(
    val hub: Hub? = null,
    val items: Flow<PagingData<Item>> = flow { emit(PagingData.from(emptyList())) },
    val invitationsList: List<HubMember> = emptyList(),
    val usersList: List<UserSearch> = emptyList(),
    val currentItem: Item? = null,
    val selectedCategory: String? = null,
    val selectedManufacturer: String? = null,
    val currentSearchQuery: String? = null,
    val hubDeletionResult: EmptyResult<NetworkError>? = null,
    val hubUpdateResult: EmptyResult<NetworkError>? = null,
    val operationResult: EmptyResult<NetworkError>? = null,
    val itemDeletionResult: EmptyResult<NetworkError>? = null,
    val hubItemsResult: EmptyResult<NetworkError>? = null,
    val invitationProcessResult: EmptyResult<NetworkError>? = null
)