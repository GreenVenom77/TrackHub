package com.trackhub.feat_hub.presentation.models

data class ItemUI(
    val id: Int = 0,
    val hubId: String,
    val name: String,
    val stockCount: String,
    val unit: String,
    val imageUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)