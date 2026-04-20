package com.greenvenom.core_auth.data.repository

import com.greenvenom.core_util.input.domain.ValidationError
import com.greenvenom.core_util.input.domain.ValidationResult

data class EmailState(
    val email: String? = null,
    val emailValidity: ValidationResult<Unit, ValidationError>? = null,
)