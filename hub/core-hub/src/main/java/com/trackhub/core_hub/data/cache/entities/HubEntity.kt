package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trackhub.core_hub.domain.HubRole

@Entity(tableName = "hubs")
data class HubEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "is_owned")
    val isOwned: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "hub_role")
    val hubRole: HubRole,
    @ColumnInfo(name = "manufacturer_list")
    val manufacturerList: List<String>,
    @ColumnInfo(name = "category_list")
    val categoryList: List<String>
)