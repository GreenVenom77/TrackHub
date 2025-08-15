package com.trackhub.feat_hub.data.cache

import com.trackhub.core_hub.data.cache.dao.HubDao
import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.ItemEntity
import com.trackhub.core_hub.data.mappers.extractHub
import com.trackhub.core_hub.data.mappers.extractItem
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HubRoomDataSource(
    private val hubDao: HubDao
): HubCacheDataSource {
    // Hubs
    override suspend fun addHub(hub: HubEntity) {
        hubDao.addHub(hub)
    }

    override suspend fun updateHub(hub: HubEntity) {
        hubDao.updateHub(hub)
    }

    override suspend fun deleteHub(hubId: String) {
        hubDao.deleteHub(hubId)
    }

    override suspend fun deleteHubs(hubs: List<HubEntity>) {
        hubDao.deleteHubs(hubs)
    }

    override suspend fun updateOwnHubs(hubs: List<HubEntity>) {
        hubDao.updateOwnHubs(hubs)
    }

    override suspend fun updateSharedHubs(hubs: List<HubEntity>) {
        hubDao.updateSharedHubs(hubs)
    }

    override suspend fun getHub(hubId: String): HubEntity {
        return hubDao.getHub(hubId)
    }

    override fun getOwnHubs(): Flow<List<Hub>> {
        return hubDao.getOwnHubs().map {
            it.map { hubEntity ->
                hubEntity.extractHub()
            }
        }
    }

    override fun getSharedHubs(): Flow<List<Hub>> {
        return hubDao.getSharedHubs().map {
            it.map { hubEntity ->
                hubEntity.extractHub()
            }
        }
    }

    override suspend fun deleteAllHubs() {
        hubDao.deleteAllHubs()
    }

    // Items
    override suspend fun updateHubItems(items: List<ItemEntity>) {
        hubDao.updateHubItems(items)
    }

    override suspend fun deleteItems(items: List<ItemEntity>) {
        hubDao.deleteItems(items)
    }

    override fun getItemsFromHub(hubId: String): Flow<List<Item>> {
        return hubDao.getItemsFromHub(hubId).map {
            it.map{ hubItemEntity ->
                hubItemEntity.extractItem()
            }
        }
    }
}