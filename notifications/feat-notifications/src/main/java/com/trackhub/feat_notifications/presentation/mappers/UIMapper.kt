package com.trackhub.feat_notifications.presentation.mappers

import com.greenvenom.core_ui.utils.formatDateTime
import com.trackhub.core_notifications.domain.models.HubInvitation
import com.trackhub.feat_notifications.presentation.models.HubInvitationUI

fun HubInvitation.toUI(): HubInvitationUI {
    return HubInvitationUI(
        invitationId = invitationId,
        hubName = hubName,
        hubDescription = hubDescription,
        inviterName = inviterName,
        inviterEmail = inviterEmail,
        hubRole = hubRole,
        formattedDate = formatDateTime(createdAt)
    )
}