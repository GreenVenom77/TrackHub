package com.trackhub.feat_hub.presentation.models

import androidx.compose.ui.graphics.Color

data class UserSearchUI(
    val userId: String,
    val displayName: String,
    val email: String,
    val statusText: String,
    val statusColor: Color
)