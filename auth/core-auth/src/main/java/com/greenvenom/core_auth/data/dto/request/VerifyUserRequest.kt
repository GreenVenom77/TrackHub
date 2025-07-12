package com.greenvenom.core_auth.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyUserRequest(
    val email: String,
    val otp: String
)
