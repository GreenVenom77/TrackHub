package com.greenvenom.feat_auth.data.repo

import com.greenvenom.core_auth.data.dto.request.LoginRequest
import com.greenvenom.core_auth.data.dto.request.RegisterRequest
import com.greenvenom.core_auth.data.dto.request.ResetPasswordRequest
import com.greenvenom.core_auth.data.dto.request.UpdatePasswordRequest
import com.greenvenom.core_auth.data.dto.request.VerifyUserRequest
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.feat_auth.domain.remote.AuthRemoteDataSource
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import io.github.jan.supabase.auth.user.UserInfo

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
): AuthRepository {
    override suspend fun registerUser(
        email: String,
        password: String,
        fullName: String
    ): NetworkResult<UserInfo?, NetworkError> {
        return remoteDataSource.registerUser(
            RegisterRequest(email, password, fullName)
        )
    }

    override suspend fun loginUser(
        email: String,
        password: String
    ): EmptyResult<NetworkError> {
        return remoteDataSource.loginUser(
            LoginRequest(email, password)
        )
    }

    override suspend fun verifyUser(
        email: String,
        token: String
    ): EmptyResult<NetworkError> {
        return remoteDataSource.verifyUser(
            VerifyUserRequest(email, token)
        )
    }

    override suspend fun sendResetPasswordEmail(
        email: String
    ): EmptyResult<NetworkError> {
        return remoteDataSource.sendResetPasswordEmail(
            ResetPasswordRequest(email)
        )
    }

    override suspend fun updatePassword(
        newPassword: String
    ): NetworkResult<UserInfo, NetworkError> {
        return remoteDataSource.updatePassword(
            UpdatePasswordRequest(newPassword)
        )
    }
}