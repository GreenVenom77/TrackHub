package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResponse(
    val userId: String,
    val displayName: String,
    val email: String,
    val currentStatus: String
)