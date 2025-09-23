package com.seravian.feat_local.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seravian.core_local.utils.ListStringConverters
import com.trackhub.core_hub.data.cache.dao.HubDao
import com.trackhub.core_hub.data.cache.dao.ItemDao
import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.cache.entities.ItemEntity

@Database(
    entities = [HubEntity::class, ItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListStringConverters::class)
abstract class TrackHubDatabase: RoomDatabase() {
    abstract val hubDao: HubDao

    abstract val itemDao: ItemDao
}