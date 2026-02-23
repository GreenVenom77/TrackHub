package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.cache.entities.HubEntity
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.response.HubResponse
import com.trackhub.core_hub.data.remote.dto.response.OwnedHubResponse
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.models.Hub

fun HubResponse.extractHub(): Hub {
    return Hub(
        id = hubId,
        ownerId = ownerId,
        viewerId = viewerId,
        name = hubName,
        description = description,
        createdAt = createdAt,
        role = HubRole.valueOf(hubRole),
        manufacturerList = manufacturerList,
        categoryList = categoryList
    )
}

fun OwnedHubResponse.extractHub(): Hub {
    return Hub(
        id = this.id,
        ownerId = this.ownerId,
        viewerId = this.viewerId,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        role = HubRole.Owner,
        manufacturerList = this.manufacturerList,
        categoryList = this.categoryList
    )
}

fun Hub.toHubEntity(): HubEntity {
    return HubEntity(
        id = this.id,
        ownerId = this.ownerId,
        viewerId = this.viewerId,
        name = this.name,
        description = this.description,
        isOwned = this.role == HubRole.Owner,
        createdAt = this.createdAt,
        hubRole = this.role,
        manufacturerList = this.manufacturerList,
        categoryList = this.categoryList
    )
}

fun HubEntity.extractHub(): Hub {
    return Hub(
        id = this.id,
        ownerId = this.ownerId,
        viewerId = this.viewerId,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt,
        role = this.hubRole,
        manufacturerList = this.manufacturerList,
        categoryList = this.categoryList
    )
}

fun Hub.toUpdateRequest(): HubUpdateRequest {
    return HubUpdateRequest(
        id = this.id,
        name = this.name,
        description = this.description,
        manufacturerList = this.manufacturerList,
        categoryList = this.categoryList
    )
}