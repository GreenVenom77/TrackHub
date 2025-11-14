package com.trackhub.core_hub.data.remote.dto.request

import com.trackhub.core_hub.domain.HubRole
import kotlinx.serialization.Serializable

@Serializable
data class UserInviteRequest(
    val hubId: String,
    val userId: String,
    val roleName: HubRole
)
