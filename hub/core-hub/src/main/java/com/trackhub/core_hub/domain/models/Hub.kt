package com.trackhub.core_hub.domain.models

import com.trackhub.core_hub.domain.HubRole

data class Hub(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String?,
    val createdAt: String,
    val role: HubRole,
    val manufacturerList: List<String>,
    val categoryList: List<String>
)