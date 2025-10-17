package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "items_fts")
@Fts4(contentEntity = ItemEntity::class)
data class ItemFts(
    @ColumnInfo(name = "rowid")
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "manufacturer")
    val manufacturer: String?,
    @ColumnInfo(name = "category")
    val category: String?
)