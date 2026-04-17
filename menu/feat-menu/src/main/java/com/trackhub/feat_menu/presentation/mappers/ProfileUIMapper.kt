package com.trackhub.feat_menu.presentation.mappers

import com.greenvenom.core_ui.utils.formatDateTime
import com.trackhub.core_menu.domain.Profile
import com.trackhub.feat_menu.presentation.models.ProfileUI

fun Profile.toUI(): ProfileUI {
    return ProfileUI(
        name = name,
        email = email,
        createdAt = formatDateTime(createdAt, false)
    )
}