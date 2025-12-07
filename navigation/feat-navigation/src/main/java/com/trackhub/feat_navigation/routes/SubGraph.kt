package com.trackhub.feat_navigation.routes

import com.greenvenom.core_navigation.domain.Destination
import com.greenvenom.core_navigation.domain.DestinationType
import kotlinx.serialization.Serializable

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
}