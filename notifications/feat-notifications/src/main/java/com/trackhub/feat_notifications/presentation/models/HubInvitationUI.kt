package com.trackhub.feat_notifications.presentation.models

import com.trackhub.core_hub.domain.enums.HubRole

data class HubInvitationUI(
    val invitationId: String,
    val hubName: String,
    val hubDescription: String?,
    val inviterName: String,
    val inviterEmail: String,
    val hubRole: HubRole,
    val formattedDate: String
)
