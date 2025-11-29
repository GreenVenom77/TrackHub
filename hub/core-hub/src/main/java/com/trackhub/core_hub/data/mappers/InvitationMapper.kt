package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.remote.dto.response.InvitationResponse
import com.trackhub.core_hub.domain.InvitationStatus
import com.trackhub.core_hub.domain.models.InvitationResult

fun InvitationResponse.toDomain(): InvitationResult {
    return InvitationResult(
        success = success,
        message = message,
        invitationStatus = InvitationStatus.fromValue(invitationStatus)
    )
}