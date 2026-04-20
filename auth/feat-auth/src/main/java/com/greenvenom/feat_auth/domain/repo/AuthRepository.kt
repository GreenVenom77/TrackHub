package com.greenvenom.feat_auth.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import io.github.jan.supabase.auth.user.UserInfo

interface AuthRepository {
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String
    ): NetworkResult<UserInfo?, NetworkError>

    suspend fun loginUser(
        email: String,
        password: String
    ): EmptyResult<NetworkError>

    suspend fun verifyUser(
        email: String,
        token: String
    ): EmptyResult<NetworkError>

    suspend fun sendResetPasswordEmail(
        email: String
    ): EmptyResult<NetworkError>

    suspend fun updatePassword(
        newPassword: String
    ): NetworkResult<UserInfo, NetworkError>
}