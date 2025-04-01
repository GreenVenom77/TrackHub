package com.trackhub.feat_network.domain.cache

import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow

interface CacheDataSource {
    suspend fun addHub(hub: Hub)
    suspend fun updateHub(hub: Hub)
    suspend fun deleteHub(hubId: String)
    suspend fun updateOwnHubs(hubs: List<Hub>)
    suspend fun updateSharedHubs(hubs: List<Hub>)
    fun getOwnHubs(): Flow<List<Hub>>
    fun getSharedHubs(): Flow<List<Hub>>
    suspend fun deleteAllHubs()
    suspend fun updateHubItems(items: List<HubItem>)
    suspend fun deleteItems(items: List<HubItem>)
    fun getItemsFromHub(hubId: String): Flow<List<HubItem>>
}