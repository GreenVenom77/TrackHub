package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HubMemberResponse(
    val userId: String,
    val displayName: String,
    val email: String,
    val role: String,
    val currentStatus: String
)
