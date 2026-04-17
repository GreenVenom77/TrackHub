package com.trackhub.feat_menu.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.trackhub.core_menu.domain.Profile

interface MenuRepository {
    suspend fun getProfile(): Profile

    suspend fun logout(): EmptyResult<NetworkError>
}