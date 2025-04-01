package com.trackhub.core_hub.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.HubItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HubDao {
    @Insert
    suspend fun addHub(hub: HubEntity)

    @Update
    suspend fun updateHub(hub: HubEntity)

    @Query("DELETE FROM hubs WHERE id = :hubId")
    suspend fun deleteHub(hubId: String)

    @Upsert
    suspend fun updateOwnHubs(hubs: List<HubEntity>)

    @Upsert
    suspend fun updateSharedHubs(hubs: List<HubEntity>)

    @Query("SELECT * FROM hubs WHERE is_owned = 1")
    fun getOwnHubs(): Flow<List<HubEntity>>

    @Query("SELECT * FROM hubs WHERE is_owned = 0")
    fun getSharedHubs(): Flow<List<HubEntity>>

    @Query("DELETE FROM hubs")
    suspend fun deleteAllHubs()

    @Upsert
    suspend fun updateHubItems(items: List<HubItemEntity>)

    @Delete
    suspend fun deleteItems(items: List<HubItemEntity>)

    @Query("SELECT * FROM hub_items WHERE hub_id = :hubId")
    fun getItemsFromHub(hubId: String): Flow<List<HubItemEntity>>
}