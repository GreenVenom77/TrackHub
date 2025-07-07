package com.greenvenom.feat_auth.domain.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult

interface AuthRemoteDataSource {
    suspend fun registerUser(
        displayName: String,
        email: String,
        password: String
    ): NetworkResult<Any?, NetworkError>

    suspend fun loginUser(email: String, password: String): NetworkResult<Any, NetworkError>

    suspend fun verifyOtp(email: String, otp: String): NetworkResult<Any, NetworkError>

    suspend fun sendResetPasswordEmail(email: String): NetworkResult<Any, NetworkError>

    suspend fun updatePassword(password: String): NetworkResult<Any, NetworkError>
}