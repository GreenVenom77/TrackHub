package com.trackhub.core_notifications.domain.models

import com.trackhub.core_hub.domain.enums.HubRole

data class HubInvitation(
    val invitationId: String,
    val hubId: String,
    val hubName: String,
    val hubDescription: String?,
    val inviterName: String,
    val inviterEmail: String,
    val hubRole: HubRole,
    val createdAt: String
)