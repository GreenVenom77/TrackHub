package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = HubEntity::class,
            parentColumns = ["id"],
            childColumns = ["hub_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("hub_id"),
        Index("name"),
        Index("manufacturer"),
        Index("category"),
    ]
)
data class ItemEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = Uuid.random().toString(),
    @ColumnInfo(name = "hub_id")
    val hubId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "stock_count")
    val stockCount: Float,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: String = Clock.System.now().toString(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: String?,
    @ColumnInfo(name = "manufacturer")
    val manufacturer: String?,
    @ColumnInfo(name = "category")
    val category: String?,
    @ColumnInfo(name = "in_stock")
    val inStock: Boolean
)