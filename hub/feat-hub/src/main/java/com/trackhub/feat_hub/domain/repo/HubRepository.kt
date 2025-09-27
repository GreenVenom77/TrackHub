package com.trackhub.feat_hub.domain.repo

import androidx.paging.PagingData
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.request.HubInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemUpdateRequest
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface HubRepository {
    fun refreshHubs()

    suspend fun addHub(hubInsertRequest: HubInsertRequest): EmptyResult<NetworkError>

    suspend fun updateHub(hubUpdateRequest: HubUpdateRequest): EmptyResult<NetworkError>

    suspend fun deleteHub(hubId: String): EmptyResult<NetworkError>

    suspend fun getHub(hubId: String): Hub

    fun getHubs(isOwned: Boolean = true): Flow<NetworkResult<List<Hub>, NetworkError>>

    suspend fun addItemToHub(itemInsertRequest: ItemInsertRequest): EmptyResult<NetworkError>

    suspend fun updateItem(itemUpdateRequest: ItemUpdateRequest): EmptyResult<NetworkError>

    suspend fun deleteHubItem(hubItemId: Int): EmptyResult<NetworkError>

    fun getItemsFromHub(
        hubId: String,
        category: String?,
        manufacturer: String?
    ): Flow<NetworkResult<Flow<PagingData<Item>>, NetworkError>>
}