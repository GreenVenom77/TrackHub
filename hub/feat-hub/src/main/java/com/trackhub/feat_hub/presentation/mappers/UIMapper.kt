package com.trackhub.feat_hub.presentation.mappers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.greenvenom.core_ui.utils.formatDateTime
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.core_hub.domain.models.UserSearch
import com.trackhub.feat_hub.presentation.models.HubUI
import com.trackhub.feat_hub.presentation.models.ItemUI
import com.trackhub.feat_hub.presentation.models.UserSearchUI

fun Hub.toHubUI(): HubUI {
    return HubUI(
        id = this.id,
        userId = this.ownerId,
        name = this.name,
        description = this.description,
        createdAt = formatDateTime(
            this.createdAt,
            withTime = false
        ),
        role = this.role.value
    )
}

fun Item.toHubItemUI(): ItemUI {
    return ItemUI(
        id = this.id,
        hubId = this.hubId,
        name = this.name,
        stockCount = this.stockCount.toString(),
        unit = this.unit,
        imageUrl = this.imageUrl,
        createdAt = formatDateTime(
            this.createdAt,
            withTime = true,
            timeBelowDate = true
        ),
        updatedAt = this.updatedAt?.let {
            formatDateTime(
                this.createdAt,
                withTime = true,
                timeBelowDate = true
            )
        },
        manufacturer = this.manufacturer ?: "",
        category = this.category ?: "",
        inStock = inStock
    )
}

@Composable
fun UserSearch.toUI(): UserSearchUI {
    val context = LocalContext.current
    val statusText = context.getString(this.currentStatus.value)
    val statusColor = when (this.currentStatus) {
        MemberStatus.Member -> MaterialTheme.colorScheme.primary
        MemberStatus.PendingInvitation -> MaterialTheme.colorScheme.tertiary
        MemberStatus.InvitationDeclined -> MaterialTheme.colorScheme.error
        MemberStatus.NotInvited -> MaterialTheme.colorScheme.secondary
    }

    return UserSearchUI(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        statusText = statusText,
        statusColor = statusColor
    )
}