package com.trackhub.feat_hub.presentation.mappers

import com.greenvenom.core_ui.utils.formatDateTime
import com.trackhub.core_hub.domain.enums.InvitationStatus
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.InvitationResult
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.core_hub.domain.models.UserSearch
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.HubUI
import com.trackhub.feat_hub.presentation.models.ItemUI
import com.trackhub.feat_hub.presentation.models.LocalizedInvitationResult
import com.trackhub.feat_hub.presentation.models.UserSearchUI
import com.trackhub.feat_hub.presentation.utils.getErrorMessageResId

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

fun UserSearch.toUI(): UserSearchUI {
    return UserSearchUI(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        statusTextResId = currentStatus.value,
        currentStatus = this.currentStatus
    )
}

fun InvitationResult.toUI(): LocalizedInvitationResult {
    return LocalizedInvitationResult(
        success = this.success,
        messageResId = when (this.invitationStatus) {
            InvitationStatus.ROLE_UPDATED -> R.string.invitation_role_updated
            InvitationStatus.INVITATION_UPDATED -> R.string.invitation_updated
            InvitationStatus.INVITATION_RESENT -> R.string.invitation_resent
            InvitationStatus.INVITATION_SENT -> R.string.invitation_sent
            InvitationStatus.ERROR -> getErrorMessageResId(message)
        },
        status = this.invitationStatus
    )
}
