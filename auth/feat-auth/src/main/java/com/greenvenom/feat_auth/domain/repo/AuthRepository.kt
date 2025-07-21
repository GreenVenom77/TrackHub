package com.greenvenom.feat_auth.domain.repo

import com.greenvenom.core_auth.data.dto.request.LoginRequest
import com.greenvenom.core_auth.data.dto.request.RegisterRequest
import com.greenvenom.core_auth.data.dto.request.ResetPasswordRequest
import com.greenvenom.core_auth.data.dto.request.UpdatePasswordRequest
import com.greenvenom.core_auth.data.dto.request.VerifyUserRequest
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import io.github.jan.supabase.auth.user.UserInfo

interface AuthRepository {
    suspend fun registerUser(
        registerRequest: RegisterRequest
    ): NetworkResult<UserInfo?, NetworkError>

    suspend fun loginUser(
        loginRequest: LoginRequest
    ): EmptyResult<NetworkError>

    suspend fun verifyUser(
        verifyUserRequest: VerifyUserRequest
    ): EmptyResult<NetworkError>

    suspend fun sendResetPasswordEmail(
        resetPasswordRequest: ResetPasswordRequest
    ): EmptyResult<NetworkError>

    suspend fun updatePassword(
        updatePasswordRequest: UpdatePasswordRequest
    ): NetworkResult<UserInfo, NetworkError>
}