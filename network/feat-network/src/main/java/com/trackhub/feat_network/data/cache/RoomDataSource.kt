package com.trackhub.feat_network.data.cache

import com.trackhub.feat_network.domain.cache.CacheDataSource
import com.trackhub.core_hub.data.cache.dao.HubDao
import com.trackhub.core_hub.data.cache.entities.toHub
import com.trackhub.core_hub.data.cache.entities.toHubEntity
import com.trackhub.core_hub.data.cache.entities.toHubItem
import com.trackhub.core_hub.data.cache.entities.toHubItemEntity
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDataSource(
    private val hubDao: HubDao
): CacheDataSource {
    // Hubs
    override suspend fun addHub(hub: Hub) {
        hubDao.addHub(hub.toHubEntity())
    }

    override suspend fun updateHub(hub: Hub) {
        hubDao.updateHub(hub.toHubEntity())
    }

    override suspend fun deleteHub(hubId: String) {
        hubDao.deleteHub(hubId)
    }

    override suspend fun updateOwnHubs(hubs: List<Hub>) {
        hubDao.updateOwnHubs(hubs.map { it.toHubEntity() })
    }

    override suspend fun updateSharedHubs(hubs: List<Hub>) {
        hubDao.updateSharedHubs(hubs.map { it.toHubEntity(false) })
    }

    override fun getOwnHubs(): Flow<List<Hub>> {
        return hubDao.getOwnHubs().map { it.map { hubEntity -> hubEntity.toHub() } }
    }

    override fun getSharedHubs(): Flow<List<Hub>> {
        return hubDao.getSharedHubs().map { it.map { hubEntity -> hubEntity.toHub() } }
    }

    override suspend fun deleteAllHubs() {
        hubDao.deleteAllHubs()
    }

    // Items
    override suspend fun updateHubItems(items: List<HubItem>) {
        hubDao.updateHubItems(items.map { it.toHubItemEntity() })
    }

    override suspend fun deleteItems(items: List<HubItem>) {
        hubDao.deleteItems(items.map { it.toHubItemEntity() })
    }

    override fun getItemsFromHub(hubId: String): Flow<List<HubItem>> {
        return hubDao.getItemsFromHub(hubId).map { it.map{ hubItemEntity -> hubItemEntity.toHubItem() } }
    }

    // Profile
}