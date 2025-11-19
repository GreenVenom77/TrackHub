package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserInviteRequest(
    val hubId: String,
    val userId: String,
    val roleName: String
)
