package com.greenvenom.feat_menu.domain.cache

import com.greenvenom.core_menu.data.cache.entities.ProfileEntity

interface MenuCacheDataSource {
    suspend fun getProfile(): ProfileEntity?
    suspend fun saveProfile(profile: ProfileEntity)
    suspend fun deleteProfile()
}