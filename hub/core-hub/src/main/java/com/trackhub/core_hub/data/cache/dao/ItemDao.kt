package com.trackhub.core_hub.data.cache.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.trackhub.core_hub.data.cache.entities.ItemEntity

@Dao
interface ItemDao {
    @Upsert
    suspend fun updateItems(items: List<ItemEntity>)

    @Delete
    suspend fun deleteItems(items: List<ItemEntity>)

    @Query("SELECT * FROM items WHERE hub_id = :hubId ORDER BY name ASC")
    fun getItemsFromHub(hubId: String): List<ItemEntity>

    @Query("""
        SELECT * FROM items
        WHERE items.hub_id = :hubId
        AND (:category IS NULL OR TRIM(:category) = '' OR items.category = :category)
        AND (:manufacturer IS NULL OR TRIM(:manufacturer) = '' OR items.manufacturer = :manufacturer)
        AND (:inStock IS NULL OR items.in_stock = :inStock)
        AND (
            :searchQuery IS NULL
            OR TRIM(:searchQuery) = ''
            OR :searchQuery = '**'
            OR items.rowid IN (
                SELECT rowid FROM items_fts WHERE items_fts MATCH :searchQuery
            )
        )
        ORDER BY items.name ASC
    """)
    fun getItemsWithFiltersPaged(
        hubId: String,
        category: String?,
        manufacturer: String?,
        inStock: Boolean?,
        searchQuery: String?
    ): PagingSource<Int, ItemEntity>
}