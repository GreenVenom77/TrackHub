package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    val id: Int,
    val hubId: String,
    val name: String,
    val stockCount: Float,
    val unit: String,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String?,
    val manufacturer: String?,
    val category: String?,
    val inStock: Boolean
)