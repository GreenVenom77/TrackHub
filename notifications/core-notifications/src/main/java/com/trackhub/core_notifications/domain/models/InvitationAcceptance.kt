package com.trackhub.core_notifications.domain.models

import androidx.annotation.StringRes

data class InvitationAcceptance(
    val success: Boolean,
    @param:StringRes val message: Int
)
