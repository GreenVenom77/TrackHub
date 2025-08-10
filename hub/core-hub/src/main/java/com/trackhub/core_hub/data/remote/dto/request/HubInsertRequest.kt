package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class HubInsertRequest(
    var userId: String = "",
    val name: String,
    val description: String?
) {
    fun addUserId(userId: String) {
        this.userId = userId
    }
}
