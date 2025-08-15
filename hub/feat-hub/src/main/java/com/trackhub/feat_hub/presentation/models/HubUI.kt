package com.trackhub.feat_hub.presentation.models

data class HubUI(
    val id: String = "",
    val userId: String,
    val name: String,
    val description: String? = null,
    val createdAt: String = "",
)