package com.trackhub.feat_hub.presentation.models

import androidx.annotation.StringRes

data class HubUI(
    val id: String = "",
    val userId: String,
    val name: String,
    val description: String?,
    val createdAt: String,
    @param:StringRes val role: Int
)