package com.greenvenom.feat_auth.presentation.reset_password

import androidx.compose.runtime.Immutable
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.validation.domain.ValidationError
import com.greenvenom.validation.domain.ValidationResult
import io.github.jan.supabase.auth.user.UserInfo

@Immutable
data class ResetPasswordState(
    val passwordValidity: ValidationResult<Unit, ValidationError>? = null,
    val confirmPasswordValidity: ValidationResult<Unit, ValidationError>? = null,
    val passwordUpdatedNetworkResult: NetworkResult<UserInfo, NetworkError>? = null,
    val emailSentNetworkResult: EmptyResult<NetworkError>? = null
)