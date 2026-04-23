package com.trackhub.feat_navigation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen: Destination {
    @Serializable
    data object Splash: Screen() {
        override val destinationType: DestinationType = DestinationType.OTHER
    }

    @Serializable
    data object Login: Screen() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object Register: Screen() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object VerifyEmail: Screen() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object OTP: Screen() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object NewPassword: Screen() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data class OwnedHubs(val ownedHubs: Boolean = true): Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data class SharedHubs(val ownedHubs: Boolean = false): Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data object HubDetails: Screen() {
        override val destinationType: DestinationType = DestinationType.SIDE
    }

    @Serializable
    data object Notifications: Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data object Menu: Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data object Profile: Screen() {
        override val destinationType: DestinationType = DestinationType.OTHER
    }
}