package com.trackhub.feat_menu.presentation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class MenuDest: Destination {
    @Serializable
    data object Menu : MenuDest() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }
}