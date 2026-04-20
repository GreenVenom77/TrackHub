package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.remote.dto.response.HubMemberResponse
import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_hub.domain.enums.MemberStatus
import com.trackhub.core_hub.domain.models.HubMember

fun HubMemberResponse.toDomain(): HubMember {
    return HubMember(
        userId = this.userId,
        name = this.displayName,
        email = this.email,
        role = HubRole.valueOf(this.role),
        status = when (this.currentStatus) {
            "member" -> MemberStatus.Member
            "pending" -> MemberStatus.PendingInvitation
            "invitation_declined" -> MemberStatus.InvitationDeclined
            "not_invited" -> MemberStatus.NotInvited
            else -> MemberStatus.NotInvited
        }
    )
}