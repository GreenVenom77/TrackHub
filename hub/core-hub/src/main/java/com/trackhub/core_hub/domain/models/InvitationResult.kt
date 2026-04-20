package com.trackhub.core_hub.domain.models

import com.trackhub.core_hub.domain.enums.InvitationStatus
import kotlinx.serialization.Serializable

@Serializable
data class InvitationResult(
    val success: Boolean,
    val message: String,
    val invitationStatus: InvitationStatus
)