package com.trackhub.feat_menu.data.cache

import com.trackhub.core_menu.data.cache.dao.ProfileDao
import com.trackhub.core_menu.data.cache.entities.ProfileEntity
import com.trackhub.feat_menu.domain.cache.MenuCacheDataSource

class MenuRoomDataSource(
    private val profileDao: ProfileDao
): MenuCacheDataSource {
    override suspend fun getProfile(): ProfileEntity? {
        return profileDao.getProfile()
    }

    override suspend fun saveProfile(profile: ProfileEntity) {
        profileDao.insertProfile(profile)
    }

    override suspend fun deleteProfile() {
        profileDao.deleteProfile()
    }
}