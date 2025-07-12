package com.trackhub.core_hub.data.remote.dto

import com.trackhub.core_hub.domain.models.HubItem
import kotlinx.serialization.Serializable

@Serializable
data class HubItemDto(
    val id: Int = 0,
    val hubId: String,
    val name: String,
    val stockCount: Float,
    val unit: String,
    val imageUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String? = null
) {
    fun extractHubItem(): HubItem {
        return HubItem(
            id = this.id,
            hubId = this.hubId,
            name = this.name,
            stockCount = this.stockCount,
            unit = this.unit,
            imageUrl = this.imageUrl,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}