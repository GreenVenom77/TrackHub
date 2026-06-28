package com.trackhub.feat_hub.presentation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class HubDest: Destination {
    @Serializable
    data class OwnedHubs(val areHubsOwned: Boolean = true): HubDest() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data class SharedHubs(val areHubsOwned: Boolean = false): HubDest() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data class OwnedHubDetails(val hubId: String): HubDest() {
        override val destinationType: DestinationType = DestinationType.SIDE
    }

    @Serializable
    data class SharedHubDetails(val hubId: String): HubDest() {
        override val destinationType: DestinationType = DestinationType.SIDE
    }
}