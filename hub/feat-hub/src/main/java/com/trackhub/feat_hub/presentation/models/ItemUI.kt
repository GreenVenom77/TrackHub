package com.trackhub.feat_hub.presentation.models

import com.trackhub.core_hub.domain.enums.BaseUnit

data class ItemUI(
    val id: String,
    val hubId: String,
    val name: String,
    val stockCount: String,
    val unit: BaseUnit,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String?,
    val manufacturer: String?,
    val category: String?,
    val inStock: Boolean
)