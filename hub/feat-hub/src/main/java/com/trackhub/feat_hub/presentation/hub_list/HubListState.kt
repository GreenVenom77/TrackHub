package com.trackhub.feat_hub.presentation.hub_list

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.trackhub.core_hub.domain.models.Hub

data class HubListState(
    val hubs: List<Hub> = emptyList(),
    val isRefreshing: Boolean = false,
    val fetchingHubsResult: EmptyResult<NetworkError>? = null,
    val addHubResult: EmptyResult<NetworkError>? = null
)