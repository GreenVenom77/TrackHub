package com.trackhub.feat_hub.presentation.models

import com.trackhub.core_hub.domain.models.HubItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class HubItemUI(
    val id: Int = 0,
    val hubId: String,
    val name: String,
    val stockCount: String,
    val unit: String,
    val imageUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

fun HubItem.toHubItemUI(): HubItemUI {
    return HubItemUI(
        id = this.id,
        hubId = this.hubId,
        name = this.name,
        stockCount = this.stockCount.toString(),
        unit = this.unit,
        imageUrl = this.imageUrl,
        createdAt = formatHubItemDate(this.createdAt),
        updatedAt = this.updatedAt?.let { formatHubItemDate(it) }
    )
}

fun formatHubItemDate(timestamp: String): String {
    // Parse the Supabase timestamp
    val instant = Instant.parse(timestamp.substringBeforeLast(".") + "Z")

    val readableDateFormatter = DateTimeFormatter
        .ofPattern("d MMM yyyy \n h:mm a")
        .withZone(ZoneId.systemDefault())

    // Return the formatted timestamp
    return readableDateFormatter.format(instant)
}