package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class InvitationResponse(
    val success: Boolean,
    val message: String,
    val invitationStatus: String
)