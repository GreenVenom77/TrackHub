package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SharedHubResponse(
    val hubId: String,
    val hubName: String,
    val description: String,
    val hubRole: String,
    val createdAt: String
)