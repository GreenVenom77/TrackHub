package com.greenvenom.feat_auth.data.repo

import com.greenvenom.core_auth.data.dto.request.LoginRequest
import com.greenvenom.core_auth.data.dto.request.RegisterRequest
import com.greenvenom.core_auth.data.dto.request.ResetPasswordRequest
import com.greenvenom.core_auth.data.dto.request.UpdatePasswordRequest
import com.greenvenom.core_auth.data.dto.request.VerifyUserRequest
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.feat_auth.domain.remote.AuthRemoteDataSource
import io.github.jan.supabase.auth.user.UserInfo

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
): AuthRepository {
    override suspend fun registerUser(
        registerRequest: RegisterRequest
    ): NetworkResult<UserInfo?, NetworkError> {
        return remoteDataSource.registerUser(registerRequest)
    }

    override suspend fun loginUser(
        loginRequest: LoginRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.loginUser(loginRequest)
    }

    override suspend fun verifyUser(
        verifyUserRequest: VerifyUserRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.verifyUser(verifyUserRequest)
    }

    override suspend fun sendResetPasswordEmail(
        resetPasswordRequest: ResetPasswordRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.sendResetPasswordEmail(resetPasswordRequest)
    }

    override suspend fun updatePassword(
        updatePasswordRequest: UpdatePasswordRequest
    ): NetworkResult<UserInfo, NetworkError> {
        return remoteDataSource.updatePassword(updatePasswordRequest)
    }
}