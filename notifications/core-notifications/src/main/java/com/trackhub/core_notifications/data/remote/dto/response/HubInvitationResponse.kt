package com.trackhub.core_notifications.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HubInvitationResponse(
    val invitationId: Int,
    val hubId: String,
    val hubName: String,
    val hubDescription: String?,
    val inviterName: String,
    val inviterEmail: String,
    val hubRole: String,
    val createdAt: String
)
