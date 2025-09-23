package com.trackhub.core_hub.data.cache.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.trackhub.core_hub.data.cache.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Upsert
    suspend fun updateItems(items: List<ItemEntity>)

    @Delete
    suspend fun deleteItems(items: List<ItemEntity>)

    @Query("SELECT * FROM items WHERE hub_id = :hubId ORDER BY name ASC")
    fun getItemsFromHub(hubId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE hub_id = :hubId ORDER BY name ASC")
    fun getItemsFromHubPaged(hubId: String): PagingSource<Int, ItemEntity>

    @Query("""
        SELECT * FROM items 
        WHERE hub_id = :hubId 
        AND (:category IS NULL OR category = :category)
        AND (:manufacturer IS NULL OR manufacturer = :manufacturer)
        ORDER BY name ASC
    """)
    fun getItemsWithFiltersPaged(
        hubId: String,
        category: String?,
        manufacturer: String?
    ): PagingSource<Int, ItemEntity>
}