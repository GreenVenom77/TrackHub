package com.trackhub.core_notifications.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class InvitationAcceptanceRequest(
    val invitationId: String,
    val response: Boolean
)
