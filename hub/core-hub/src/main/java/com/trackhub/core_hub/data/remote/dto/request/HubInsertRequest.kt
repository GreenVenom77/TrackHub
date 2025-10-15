package com.trackhub.core_hub.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class HubInsertRequest(
    var ownerId: String = "",
    val name: String,
    val description: String?,
    val manufacturerList: List<String>,
    val categoryList: List<String>
) {
    fun addUserId(userId: String) {
        this.ownerId = userId
    }
}
