package com.trackhub.core_hub.data.remote.dto.request

import com.trackhub.core_hub.domain.MemberStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChangeMemberRoleRequest(
    val hubId: String,
    val userId: String,
    val hubRole: String,
    @Transient val status: MemberStatus = MemberStatus.Member
)
