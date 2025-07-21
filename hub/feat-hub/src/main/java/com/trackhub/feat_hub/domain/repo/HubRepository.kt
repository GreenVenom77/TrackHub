package com.trackhub.feat_hub.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow

interface HubRepository {
    fun refreshHubs()

    suspend fun addHub(hub: Hub): EmptyResult<NetworkError>

    suspend fun updateHub(hub: Hub): EmptyResult<NetworkError>

    suspend fun deleteHub(hubId: String): EmptyResult<NetworkError>

    suspend fun getHub(hubId: String): Hub

    fun getHubs(isOwned: Boolean = true): Flow<NetworkResult<List<Hub>, NetworkError>>

    suspend fun addItemToHub(hubItem: HubItem): EmptyResult<NetworkError>

    suspend fun updateItem(
        itemId: Int,
        itemName: String,
        itemStock: Float,
        unit: String
    ): EmptyResult<NetworkError>

    suspend fun deleteHubItem(hubItemId: Int): EmptyResult<NetworkError>

    fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<HubItem>, NetworkError>>
}