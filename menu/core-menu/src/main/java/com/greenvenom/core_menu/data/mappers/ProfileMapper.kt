package com.greenvenom.core_menu.data.mappers

import com.greenvenom.core_menu.data.cache.entities.ProfileEntity
import com.greenvenom.core_menu.data.remote.ProfileDto
import com.greenvenom.core_menu.domain.Profile

fun ProfileDto.toDomain(): Profile {
    return Profile(
        userId = userId,
        name = displayName,
        email = email,
        createdAt = createdAt
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        userId = userId,
        displayName = name,
        email = email,
        createdAt = createdAt
    )
}

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        userId = userId,
        name = displayName,
        email = email,
        createdAt = createdAt
    )
}