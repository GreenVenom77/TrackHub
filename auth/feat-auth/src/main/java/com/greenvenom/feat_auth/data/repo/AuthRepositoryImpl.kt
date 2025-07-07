package com.greenvenom.feat_auth.data.repo

import com.greenvenom.feat_auth.domain.repo.AuthRepository
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.feat_auth.domain.remote.AuthRemoteDataSource

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
): AuthRepository {
    override suspend fun loginUser(
        email: String,
        password: String
    ): NetworkResult<Any, NetworkError> {
        return remoteDataSource.loginUser(email, password)
    }

    override suspend fun registerUser(
        email: String,
        password: String,
        displayName: String
    ): NetworkResult<Any?, NetworkError> {
        return remoteDataSource.registerUser(email, password, displayName)
    }

    override suspend fun verifyUserRegistration(
        email: String,
        otp: String
    ): NetworkResult<Any, NetworkError> {
        return remoteDataSource.verifyOtp(email, otp)
    }

    override suspend fun sendResetPasswordEmail(email: String): NetworkResult<Any, NetworkError> {
        return remoteDataSource.sendResetPasswordEmail(email)
    }

    override suspend fun updatePassword(newPassword: String): NetworkResult<Any, NetworkError> {
        return remoteDataSource.updatePassword(password = newPassword)
    }

    override suspend fun verifyOtp(
        email: String,
        otp: String
    ): NetworkResult<Any, NetworkError> {
        return remoteDataSource.verifyOtp(email, otp)
    }
}