package com.trackhub.feat_hub.presentation.mappers

import com.greenvenom.core_ui.utils.formatDateTime
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.presentation.models.HubUI
import com.trackhub.feat_hub.presentation.models.ItemUI

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
        }
    )
}