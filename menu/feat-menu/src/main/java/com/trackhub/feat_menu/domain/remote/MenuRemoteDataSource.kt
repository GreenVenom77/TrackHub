package com.trackhub.feat_menu.domain.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError

interface MenuRemoteDataSource {
    suspend fun logoutUser(): EmptyResult<NetworkError>
}