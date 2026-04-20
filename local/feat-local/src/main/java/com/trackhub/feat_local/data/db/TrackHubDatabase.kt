package com.trackhub.feat_local.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trackhub.core_hub.data.cache.dao.HubDao
import com.trackhub.core_hub.data.cache.dao.ItemDao
import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.ItemEntity
import com.trackhub.core_hub.data.cache.entities.ItemFts
import com.trackhub.core_local.utils.ListStringConverters
import com.trackhub.core_menu.data.cache.dao.ProfileDao
import com.trackhub.core_menu.data.cache.entities.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        HubEntity::class,
        ItemEntity::class,
        ItemFts::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
)
@TypeConverters(ListStringConverters::class)
abstract class TrackHubDatabase: RoomDatabase() {
    abstract val profileDao: ProfileDao

    abstract val hubDao: HubDao

    abstract val itemDao: ItemDao
}