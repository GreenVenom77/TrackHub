package com.trackhub.core_hub.domain.models

import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.MemberStatus

data class HubMember(
    val userId: String,
    val name: String,
    val email: String,
    val role: HubRole,
    val status: MemberStatus
)
