package com.trackhub.feat_hub.presentation.models

import androidx.annotation.StringRes
import com.trackhub.core_hub.domain.enums.InvitationStatus

data class LocalizedInvitationResult(
    val success: Boolean,
    @param:StringRes val messageResId: Int,
    val status: InvitationStatus
)