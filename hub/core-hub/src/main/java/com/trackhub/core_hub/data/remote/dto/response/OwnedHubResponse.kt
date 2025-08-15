package com.trackhub.core_hub.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OwnedHubResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String?,
    val createdAt: String,
    val manufacturerList: List<String>?,
    val categoryList: List<String>?
)