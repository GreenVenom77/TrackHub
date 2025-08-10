package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ItemInsertRequest(
    val hubId: String,
    val name: String,
    val stockCount: Float,
    val unit: String,
    val manufacturer: String?,
    val category: String?
)
