package com.greenvenom.feat_menu.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUI(
    val name: String,
    val email: String,
    val createdAt: String
)
