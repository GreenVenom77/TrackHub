package com.greenvenom.core_menu.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val userId: String,
    val displayName: String,
    val email: String,
    val createdAt: String
)
