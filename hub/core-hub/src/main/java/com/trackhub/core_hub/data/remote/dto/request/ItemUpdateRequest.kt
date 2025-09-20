package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ItemUpdateRequest(
    @Transient val id: Int = 0,
    val name: String,
    val stockCount: Float,
    val unit: String,
    val imageUrl: String?,
    val manufacturer: String?,
    val category: String?
)
