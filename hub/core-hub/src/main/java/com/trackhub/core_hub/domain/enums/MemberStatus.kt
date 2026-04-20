package com.trackhub.core_hub.domain.enums

import androidx.annotation.StringRes
import com.trackhub.core_hub.R

enum class MemberStatus(@param:StringRes val value: Int) {
    Member(R.string.member),
    PendingInvitation(R.string.pending_invitation),
    InvitationDeclined(R.string.invitation_declined),
    NotInvited(R.string.not_invited)
}