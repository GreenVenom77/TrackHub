package com.greenvenom.feat_auth.presentation.routes

import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.routes.DestinationType
import kotlinx.serialization.Serializable

@Serializable
sealed class AuthDest: Destination {
    @Serializable
    data object Splash: AuthDest() {
        override val destinationType: DestinationType = DestinationType.OTHER
    }

    @Serializable
    data object Login: AuthDest() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object Register: AuthDest() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object VerifyEmail: AuthDest() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data class OTP(val next: OTPNext): AuthDest() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }

    @Serializable
    data object NewPassword: AuthDest() {
        override val destinationType: DestinationType = DestinationType.AUTH
    }
}

@Serializable
enum class OTPNext {
    ConfirmAccount,
    ResetPassword
}