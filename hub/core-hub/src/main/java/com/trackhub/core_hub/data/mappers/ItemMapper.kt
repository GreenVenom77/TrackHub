package com.trackhub.core_hub.data.mappers

import com.trackhub.core_hub.data.cache.entities.ItemEntity
import com.trackhub.core_hub.data.remote.dto.response.ItemResponse
import com.trackhub.core_hub.domain.models.Item

fun Item.toItemEntity(): ItemEntity {
    return ItemEntity(
        id = this.id,
        hubId = this.hubId,
        name = this.name,
        stockCount = this.stockCount,
        unit = this.unit,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        manufacturer = this.manufacturer,
        category = this.category,
        inStock = this.inStock
    )
}

fun ItemEntity.extractItem(): Item {
    return Item(
        id = this.id,
        hubId = this.hubId,
        name = this.name,
        stockCount = this.stockCount,
        unit = this.unit,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        manufacturer = this.manufacturer,
        category = this.category,
        inStock = this.inStock
    )
}

fun ItemResponse.extractItem(): Item {
    return Item(
        id = this.id,
        hubId = this.hubId,
        name = this.name,
        stockCount = this.stockCount,
        unit = this.unit,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        manufacturer = this.manufacturer,
        category = this.category,
        inStock = this.inStock
    )
}