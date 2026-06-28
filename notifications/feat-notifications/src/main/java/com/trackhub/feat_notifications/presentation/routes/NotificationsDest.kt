package com.trackhub.feat_notifications.presentation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationsDest: Destination {
    @Serializable
    data object Notifications : NotificationsDest() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }
}