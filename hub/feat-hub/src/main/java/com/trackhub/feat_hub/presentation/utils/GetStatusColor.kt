package com.trackhub.feat_hub.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.trackhub.core_hub.domain.enums.MemberStatus

@Composable
fun MemberStatus.getStatusColor(): Color {
    return when (this) {
        MemberStatus.Owner -> MaterialTheme.colorScheme.primary
        MemberStatus.Member -> MaterialTheme.colorScheme.primary
        MemberStatus.PendingInvitation -> MaterialTheme.colorScheme.tertiary
        MemberStatus.InvitationDeclined -> MaterialTheme.colorScheme.error
        MemberStatus.NotInvited -> MaterialTheme.colorScheme.secondary
    }
}