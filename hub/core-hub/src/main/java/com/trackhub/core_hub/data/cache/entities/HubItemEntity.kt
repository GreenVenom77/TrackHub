package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.trackhub.core_hub.domain.models.HubItem

@Entity(
    tableName = "hub_items",
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
        Index("hub_id")
    ]
)
data class HubItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "hub_id")
    val hubId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "stock_count")
    val stockCount: Float,
    @ColumnInfo(name = "unit")
    val unit: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
) {
    fun extractHubItem(): HubItem {
        return HubItem(
            id = this.id,
            hubId = this.hubId,
            name = this.name,
            stockCount = this.stockCount,
            unit = this.unit,
            imageUrl = this.imageUrl,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}