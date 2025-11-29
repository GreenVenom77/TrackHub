package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LeaveHubRequest(
    val hubId: String
)
