package com.trackhub.core_hub.data.remote.dto

import com.trackhub.core_hub.domain.models.Hub
import kotlinx.serialization.Serializable

@Serializable
data class HubDto(
    val id: String = "",
    val userId: String,
    val name: String,
    val description: String? = null,
    val createdAt: String = "",
) {
    fun extractHub(): Hub {
        return Hub(
            id = this.id,
            userId = this.userId,
            name = this.name,
            description = this.description,
            createdAt = this.createdAt
        )
    }
}