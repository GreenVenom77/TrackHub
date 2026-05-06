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
    data class OTP(val next: OTPNext): Screen() {
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
    data class OwnedHubDetails(val hubId: String): Screen() {
        override val destinationType: DestinationType = DestinationType.SIDE
    }

    @Serializable
    data class SharedHubDetails(val hubId: String): Screen() {
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

@Serializable
enum class OTPNext {
    ConfirmAccount,
    ResetPassword
}