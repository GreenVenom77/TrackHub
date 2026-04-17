package com.trackhub.core_hub.data.cache.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_menu.data.cache.entities.ProfileEntity
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Entity(
    tableName = "hubs",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["viewer_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("viewer_id")
    ]
)
data class HubEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = Uuid.random().toString(),
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    @ColumnInfo(
        name = "viewer_id",
        defaultValue = ""
    )
    val viewerId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "is_owned")
    val isOwned: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: String = Clock.System.now().toString(),
    @ColumnInfo(name = "hub_role")
    val hubRole: HubRole,
    @ColumnInfo(name = "manufacturer_list")
    val manufacturerList: List<String>,
    @ColumnInfo(name = "category_list")
    val categoryList: List<String>
)