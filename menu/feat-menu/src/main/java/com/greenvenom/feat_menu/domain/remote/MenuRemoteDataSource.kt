package com.greenvenom.feat_menu.domain.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult

interface MenuRemoteDataSource {
    suspend fun logoutUser(): NetworkResult<Any, NetworkError>
}