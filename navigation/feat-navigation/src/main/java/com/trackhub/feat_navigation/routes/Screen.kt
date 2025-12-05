package com.trackhub.feat_navigation.routes

import com.greenvenom.core_navigation.domain.Destination
import com.greenvenom.core_navigation.domain.DestinationType
import kotlinx.serialization.Serializable

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
    data object MyHubs: Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
    }

    @Serializable
    data class MyHubDetails(val hubId: String = ""): Screen() {
        override val destinationType: DestinationType = DestinationType.SIDE
    }

    @Serializable
    data object SharedHubs: Screen() {
        override val destinationType: DestinationType = DestinationType.MAIN
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