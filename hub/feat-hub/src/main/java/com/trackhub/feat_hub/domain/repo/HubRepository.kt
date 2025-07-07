package com.trackhub.feat_hub.domain.repo

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow

interface HubRepository {
    fun refreshHubs()

    suspend fun addHub(hub: Hub): NetworkResult<Unit, NetworkError>

    suspend fun updateHub(hub: Hub): NetworkResult<Unit, NetworkError>

    suspend fun deleteHub(hubId: String): NetworkResult<Unit, NetworkError>

    fun getHubs(isOwned: Boolean = true): Flow<NetworkResult<List<Hub>, NetworkError>>

    suspend fun addItemToHub(hubItem: HubItem): NetworkResult<Unit, NetworkError>

    suspend fun updateItem(
        itemId: Int,
        itemName: String,
        itemStock: Float,
        unit: String
    ): NetworkResult<Unit, NetworkError>

    suspend fun deleteHubItem(hubItemId: Int): NetworkResult<Unit, NetworkError>

    fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<HubItem>, NetworkError>>
}