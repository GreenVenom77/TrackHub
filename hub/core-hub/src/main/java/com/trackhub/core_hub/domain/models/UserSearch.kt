package com.trackhub.core_hub.domain.models

import com.trackhub.core_hub.domain.MemberStatus

data class UserSearch(
    val userId: String,
    val displayName: String,
    val email: String,
    val currentStatus: MemberStatus
)
