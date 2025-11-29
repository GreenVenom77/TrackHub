package com.trackhub.feat_hub.presentation.models

import androidx.annotation.StringRes
import com.trackhub.core_hub.domain.MemberStatus

data class UserSearchUI(
    val userId: String,
    val displayName: String,
    val email: String,
    @param:StringRes val statusTextResId: Int,
    val currentStatus: MemberStatus
)