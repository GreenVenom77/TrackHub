package com.trackhub.feat_hub.domain.repo

import androidx.paging.PagingData
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface HubRepository {
    fun refreshHubs()

    fun refreshHub(hubId: String)

    suspend fun addHub(
        ownerId: String,
        name: String,
        description: String?,
        manufacturerList: List<String>,
        categoryList: List<String>
    ): EmptyResult<NetworkError>

    suspend fun updateHub(
        id: String,
        name: String,
        description: String?,
        manufacturerList: List<String>,
        categoryList: List<String>
    ): NetworkResult<Hub, NetworkError>

    suspend fun deleteHub(hubId: String): EmptyResult<NetworkError>

    suspend fun leaveHub(hubId: String): EmptyResult<NetworkError>

    suspend fun getHub(hubId: String): Hub

    fun getHubs(areOwned: Boolean = true): Flow<NetworkResult<List<Hub>, NetworkError>>

    suspend fun addItemToHub(
        hubId: String,
        name: String,
        stockCount: Float,
        unit: String,
        manufacturer: String?,
        category: String?
    ): EmptyResult<NetworkError>

    suspend fun updateItem(
        id: String,
        name: String,
        stockCount: Float,
        unit: String,
        imageUrl: String?,
        manufacturer: String?,
        category: String?
    ): EmptyResult<NetworkError>

    suspend fun deleteHubItem(hubItemId: String): EmptyResult<NetworkError>

    suspend fun syncHub(hubId: String): EmptyResult<NetworkError>

    fun getItemsFromHub(
        hubId: String,
        category: String?,
        manufacturer: String?,
        inStock: Boolean?,
        searchQuery: String?
    ): Flow<NetworkResult<Flow<PagingData<Item>>, NetworkError>>
}
