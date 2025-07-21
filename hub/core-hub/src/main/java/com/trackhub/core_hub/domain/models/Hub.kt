package com.trackhub.core_hub.domain.models

import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.remote.dto.HubDto
import kotlinx.serialization.Serializable

@Serializable
data class Hub(
    val id: String = "",
    val userId: String = "",
    val name: String,
    val description: String? = null,
    val createdAt: String = "",
) {
    fun toHubDto(): HubDto {
        return HubDto(
            id = this.id,
            userId = this.userId,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt
        )
    }

    fun toHubEntity(isOwned: Boolean = true): HubEntity {
        return HubEntity(
            id = this.id,
            userId = this.userId,
            name = this.name,
            description = this.description,
            isOwned = isOwned,
            createdAt = this.createdAt,
        )
    }
}