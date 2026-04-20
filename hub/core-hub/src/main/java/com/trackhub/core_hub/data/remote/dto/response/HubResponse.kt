package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class HubResponse(
    val hubId: String,
    val ownerId: String,
    @Transient val viewerId: String = "",
    val hubName: String,
    val description: String?,
    val hubRole: String,
    val createdAt: String,
    val manufacturerList: List<String>,
    val categoryList: List<String>
)