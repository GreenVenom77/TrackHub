package com.trackhub.core_hub.data.remote.dto.request

import com.trackhub.core_hub.domain.enums.MemberStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChangeMemberRoleRequest(
    val hubId: String,
    val invitedUserId: String,
    val hubRole: String,
    @Transient val status: MemberStatus = MemberStatus.Member
)
