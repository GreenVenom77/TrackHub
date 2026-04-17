package com.trackhub.feat_menu.domain.cache

import com.trackhub.core_menu.data.cache.entities.ProfileEntity

interface MenuCacheDataSource {
    suspend fun getProfile(): ProfileEntity?
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun deleteProfile()
}