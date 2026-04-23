package com.trackhub.feat_navigation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class SubGraph: Destination {
    @Serializable
    data object Auth: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data class HubDetails(val hubId: String = ""): SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data object Main: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data object OwnedHubs: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data object SharedHubs: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data object Notifications: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }

    @Serializable
    data object Menu: SubGraph() {
        override val destinationType: DestinationType = DestinationType.GRAPH
    }
}