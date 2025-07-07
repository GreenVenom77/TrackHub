package com.trackhub.feat_hub.domain.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow

interface HubRemoteDataSource {
    suspend fun addHub(hub: Hub): NetworkResult<Hub, NetworkError>

    suspend fun updateHub(hub: Hub): NetworkResult<Hub, NetworkError>

    suspend fun deleteHub(hubId: String): NetworkResult<Unit, NetworkError>

    suspend fun getOwnHubs(): NetworkResult<List<Hub>, NetworkError>

    suspend fun getSharedHubs(): NetworkResult<List<Hub>, NetworkError>

    suspend fun addItemToHub(hubItem: HubItem): NetworkResult<Unit, NetworkError>

    suspend fun updateItem(hubItem: HubItem): NetworkResult<Unit, NetworkError>

    suspend fun deleteItem(itemId: Int): NetworkResult<Unit, NetworkError>

    suspend fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<HubItem>, NetworkError>>
}