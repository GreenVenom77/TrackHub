package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trackhub.core_hub.domain.models.Hub

@Entity(tableName = "hubs")
data class HubEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "is_owned")
    val isOwned: Boolean = true,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
) {
    fun extractHub(): Hub {
        return Hub(
            id = this.id,
            userId = this.userId,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt,
        )
    }
}