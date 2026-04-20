package com.trackhub.core_notifications.data.mappers

import com.trackhub.core_notifications.data.remote.dto.response.InvitationAcceptanceResponse
import com.trackhub.core_notifications.data.utils.parseResponse
import com.trackhub.core_notifications.domain.models.InvitationAcceptance

fun InvitationAcceptanceResponse.toDomain(): InvitationAcceptance {
    return InvitationAcceptance(
        success = success,
        message = parseResponse(this)
    )
}