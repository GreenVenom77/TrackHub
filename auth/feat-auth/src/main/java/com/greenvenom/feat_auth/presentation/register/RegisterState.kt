package com.greenvenom.feat_auth.presentation.register

import androidx.compose.runtime.Immutable
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.validation.domain.ValidationError
import com.greenvenom.validation.domain.ValidationResult
import io.github.jan.supabase.auth.user.UserInfo

@Immutable
data class RegisterState(
    val emailValidity: ValidationResult<Unit, ValidationError>? = null,
    val usernameValidity: ValidationResult<Unit, ValidationError>? = null,
    val passwordValidity: ValidationResult<Unit, ValidationError>? = null,
    val confirmPasswordValidity: ValidationResult<Unit, ValidationError>? = null,
    val registrationNetworkResult: NetworkResult<UserInfo?, NetworkError>? = null
)