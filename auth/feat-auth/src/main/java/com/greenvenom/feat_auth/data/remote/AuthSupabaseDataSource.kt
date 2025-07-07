package com.greenvenom.feat_auth.data.remote

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
        displayName: String,
        email: String,
        password: String
    ): NetworkResult<UserInfo?, NetworkError> {
        return supabaseCall<UserInfo?> {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject { put("display_name", displayName) }
            }
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): NetworkResult<Unit, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.verifyEmailOtp(OtpType.Email.EMAIL, email, otp)
        }
    }

    override suspend fun sendResetPasswordEmail(email: String): NetworkResult<Unit, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.resetPasswordForEmail(email = email)
        }
    }

    override suspend fun updatePassword(password: String): NetworkResult<UserInfo, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.updateUser {
                this.password = password
            }
        }
    }

    override suspend fun loginUser(
        email: String,
        password: String
    ): NetworkResult<Unit, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }
}