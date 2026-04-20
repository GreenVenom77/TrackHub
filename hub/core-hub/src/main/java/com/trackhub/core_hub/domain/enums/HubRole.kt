package com.trackhub.core_hub.domain.enums

import androidx.annotation.StringRes
import com.trackhub.core_hub.R

enum class HubRole(@param:StringRes val value: Int) {
    Owner(R.string.owner),
    Editor(R.string.editor),
    Viewer(R.string.viewer),
}