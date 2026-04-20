package com.trackhub.core_notifications.data.mappers

import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_notifications.data.remote.dto.response.HubInvitationResponse
import com.trackhub.core_notifications.domain.models.HubInvitation

fun HubInvitationResponse.toDomain(): HubInvitation {
    return HubInvitation(
        invitationId = invitationId,
        hubId = hubId,
        hubName = hubName,
        hubDescription = hubDescription,
        inviterName = inviterName,
        inviterEmail = inviterEmail,
        hubRole = HubRole.valueOf(hubRole),
        createdAt = createdAt
    )
}