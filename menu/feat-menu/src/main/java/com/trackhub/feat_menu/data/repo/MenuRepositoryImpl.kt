package com.trackhub.feat_menu.data.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.onSuccess
import com.trackhub.core_menu.data.mappers.toDomain
import com.trackhub.core_menu.domain.Profile
import com.trackhub.feat_menu.domain.cache.MenuCacheDataSource
import com.trackhub.feat_menu.domain.remote.MenuRemoteDataSource
import com.trackhub.feat_menu.domain.repo.MenuRepository

class MenuRepositoryImpl(
    private val cacheDataSource: MenuCacheDataSource,
    private val remoteDataSource: MenuRemoteDataSource,
): MenuRepository {
    override suspend fun getProfile(): Profile {
        return cacheDataSource.getProfile()?.toDomain() as Profile
    }

    override suspend fun logout(): EmptyResult<NetworkError> {
        return remoteDataSource.logoutUser().onSuccess {
            cacheDataSource.deleteProfile()
        }
    }
}