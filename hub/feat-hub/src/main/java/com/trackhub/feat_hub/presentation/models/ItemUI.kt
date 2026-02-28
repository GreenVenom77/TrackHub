package com.trackhub.feat_hub.presentation.models

data class ItemUI(
    val id: String,
    val hubId: String,
    val name: String,
    val stockCount: String,
    val unit: String,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String?,
    val manufacturer: String?,
    val category: String?,
    val inStock: Boolean
)