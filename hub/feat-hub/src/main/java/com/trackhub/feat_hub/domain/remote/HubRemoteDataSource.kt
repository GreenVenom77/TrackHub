package com.trackhub.feat_hub.domain.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.request.HubInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemUpdateRequest
import com.trackhub.core_hub.data.remote.dto.response.ItemResponse
import com.trackhub.core_hub.data.remote.dto.response.OwnedHubResponse
import com.trackhub.core_hub.data.remote.dto.response.SharedHubResponse
import kotlinx.coroutines.flow.Flow

interface HubRemoteDataSource {
    suspend fun addHub(hubInsertRequest: HubInsertRequest): NetworkResult<OwnedHubResponse, NetworkError>

    suspend fun updateHub(hubUpdateRequest: HubUpdateRequest): NetworkResult<OwnedHubResponse, NetworkError>

    suspend fun deleteHub(hubId: String): EmptyResult<NetworkError>

    suspend fun getOwnHubs(): NetworkResult<List<OwnedHubResponse>, NetworkError>

    suspend fun getSharedHubs(): NetworkResult<List<SharedHubResponse>, NetworkError>

    suspend fun addItemToHub(itemInsertRequest: ItemInsertRequest): EmptyResult<NetworkError>

    suspend fun updateItem(itemUpdateRequest: ItemUpdateRequest): EmptyResult<NetworkError>

    suspend fun deleteItem(itemId: Int): EmptyResult<NetworkError>

    suspend fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<ItemResponse>, NetworkError>>
}