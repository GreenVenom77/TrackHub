package com.trackhub.feat_hub.data.cache

import androidx.paging.PagingSource
import com.trackhub.core_hub.data.cache.dao.HubDao
import com.trackhub.core_hub.data.cache.dao.ItemDao
import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.ItemEntity
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import kotlinx.coroutines.flow.Flow

class HubRoomDataSource(
    private val hubDao: HubDao,
    private val itemDao: ItemDao
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

    override fun getOwnHubs(): Flow<List<HubEntity>> {
        return hubDao.getOwnHubs()
    }

    override fun getSharedHubs(): Flow<List<HubEntity>> {
        return hubDao.getSharedHubs()
    }

    override suspend fun deleteAllHubs() {
        hubDao.deleteAllHubs()
    }

    // Items
    override suspend fun updateHubItems(items: List<ItemEntity>) {
        itemDao.updateItems(items)
    }

    override suspend fun deleteItems(items: List<ItemEntity>) {
        itemDao.deleteItems(items)
    }

    override fun getItemsFromHub(hubId: String): Flow<List<ItemEntity>> {
        return itemDao.getItemsFromHub(hubId)
    }

    override fun getItemsWithFiltersPaged(
        hubId: String,
        category: String?,
        manufacturer: String?
    ): PagingSource<Int, ItemEntity> {
        return itemDao.getItemsWithFiltersPaged(
            hubId,
            category,
            manufacturer
        )
    }
}