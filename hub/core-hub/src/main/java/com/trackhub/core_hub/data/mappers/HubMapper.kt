package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.remote.dto.response.OwnedHubResponse
import com.trackhub.core_hub.data.remote.dto.response.SharedHubResponse
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.models.Hub

fun SharedHubResponse.extractHub(): Hub {
    return Hub(
        id = hubId,
        ownerId = "",
        name = hubName,
        description = description,
        createdAt = createdAt,
        role = HubRole.valueOf(hubRole)
    )
}

fun OwnedHubResponse.extractHub(): Hub {
    return Hub(
        id = this.id,
        ownerId = this.ownerId,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        role = HubRole.Owner
    )
}

fun Hub.toHubEntity(): HubEntity {
    return HubEntity(
        id = this.id,
        ownerId = this.ownerId,
        name = this.name,
        description = this.description,
        isOwned = ownerId.isNotBlank(),
        createdAt = this.createdAt,
        hubRole = this.role
    )
}