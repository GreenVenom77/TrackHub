package com.greenvenom.feat_auth.data.remote

import com.greenvenom.core_auth.data.dto.request.LoginRequest
import com.greenvenom.core_auth.data.dto.request.RegisterRequest
import com.greenvenom.core_auth.data.dto.request.ResetPasswordRequest
import com.greenvenom.core_auth.data.dto.request.UpdatePasswordRequest
import com.greenvenom.core_auth.data.dto.request.VerifyUserRequest
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.greenvenom.feat_auth.domain.remote.AuthRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthSupabaseDataSource(
    private val supabaseClient: SupabaseClient
): AuthRemoteDataSource {
    override suspend fun registerUser(
        registerRequest: RegisterRequest
    ): NetworkResult<UserInfo?, NetworkError> {
        return supabaseCall<UserInfo?> {
            supabaseClient.auth.signUpWith(Email) {
                email = registerRequest.email
                password = registerRequest.password
                data = buildJsonObject { put("display_name", registerRequest.displayName) }
            }
        }
    }

    override suspend fun loginUser(
        loginRequest: LoginRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.auth.signInWith(Email) {
                email = loginRequest.email
                password = loginRequest.password
            }
        }
    }

    override suspend fun verifyUser(
        verifyUserRequest: VerifyUserRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = verifyUserRequest.email,
                token = verifyUserRequest.otp
            )
        }
    }

    override suspend fun sendResetPasswordEmail(
        resetPasswordRequest: ResetPasswordRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.auth.resetPasswordForEmail(
                email = resetPasswordRequest.email
            )
        }
    }

    override suspend fun updatePassword(
        updatePasswordRequest: UpdatePasswordRequest
    ): NetworkResult<UserInfo, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.updateUser {
                password = updatePasswordRequest.newPassword
            }
        }
    }
}