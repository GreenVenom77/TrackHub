package com.trackhub.feat_hub.domain.cache

import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.ItemEntity
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import kotlinx.coroutines.flow.Flow

interface HubCacheDataSource {
    suspend fun addHub(hub: HubEntity)

    suspend fun updateHub(hub: HubEntity)

    suspend fun deleteHub(hubId: String)

    suspend fun deleteHubs(hubs: List<HubEntity>)

    suspend fun updateOwnHubs(hubs: List<HubEntity>)

    suspend fun updateSharedHubs(hubs: List<HubEntity>)

    suspend fun getHub(hubId: String): HubEntity

    fun getOwnHubs(): Flow<List<Hub>>

    fun getSharedHubs(): Flow<List<Hub>>

    suspend fun deleteAllHubs()

    suspend fun updateHubItems(items: List<ItemEntity>)

    suspend fun deleteItems(items: List<ItemEntity>)

    fun getItemsFromHub(hubId: String): Flow<List<Item>>
}