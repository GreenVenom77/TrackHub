package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.remote.dto.response.UserSearchResponse
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.core_hub.domain.models.UserSearch

fun UserSearchResponse.toDomain(): UserSearch {
    return UserSearch(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        currentStatus = when (this.currentStatus) {
            "member" -> MemberStatus.Member
            "pending_invitation" -> MemberStatus.PendingInvitation
            "invitation_declined" -> MemberStatus.InvitationDeclined
            "not_invited" -> MemberStatus.NotInvited
            else -> MemberStatus.NotInvited
        }
    )
}