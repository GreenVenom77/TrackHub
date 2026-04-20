package com.trackhub.core_hub.domain.models

data class Item(
    val id: String,
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