package com.trackhub.feat_hub.domain.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.HubDto
import com.trackhub.core_hub.data.remote.dto.HubItemDto
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow

interface HubRemoteDataSource {
    suspend fun addHub(hub: Hub): NetworkResult<HubDto, NetworkError>

    suspend fun updateHub(hub: Hub): NetworkResult<HubDto, NetworkError>

    suspend fun deleteHub(hubId: String): EmptyResult<NetworkError>

    suspend fun getOwnHubs(): NetworkResult<List<HubDto>, NetworkError>

    suspend fun getSharedHubs(): NetworkResult<List<HubDto>, NetworkError>

    suspend fun addItemToHub(hubItem: HubItem): EmptyResult<NetworkError>

    suspend fun updateItem(hubItem: HubItem): EmptyResult<NetworkError>

    suspend fun deleteItem(itemId: Int): EmptyResult<NetworkError>

    suspend fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<HubItemDto>, NetworkError>>
}