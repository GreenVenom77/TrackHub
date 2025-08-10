package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class HubUpdateRequest(
    @Transient val id: String = "",
    val name: String,
    val description: String
)