package com.trackhub.core_notifications.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class InvitationAcceptanceResponse(
    val success: Boolean,
    val message: String
)