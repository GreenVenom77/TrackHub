package com.trackhub.feat_hub.presentation.utils

import androidx.annotation.StringRes
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.feat_hub.R

@StringRes fun getRoleDescription(role: HubRole): Int {
    return when (role) {
        HubRole.Owner -> R.string.role_owner_desc
        HubRole.Editor -> R.string.role_editor_desc
        HubRole.Viewer -> R.string.role_viewer_desc
    }
}