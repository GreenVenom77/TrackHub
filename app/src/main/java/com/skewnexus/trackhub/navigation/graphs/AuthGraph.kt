package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.greenvenom.feat_auth.presentation.login.LoginScreen
import com.greenvenom.feat_auth.presentation.otp.OtpScreen
import com.greenvenom.feat_auth.presentation.register.RegisterScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.NewPasswordScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.VerifyEmailScreen
import com.trackhub.feat_navigation.routes.OTPNext
import com.trackhub.feat_navigation.routes.Screen

fun EntryProviderScope<NavKey>.authGraph(
    navigate: (NavigationType) -> Unit,
) {
    entry<Screen.Login> {
        LoginScreen(
            navigateToRegisterScreen = {
                navigate(
                    NavigationType.Standard(Screen.Register)
                )
            },
            navigateToEmailVerificationScreen = {
                navigate(
                    NavigationType.Standard(Screen.VerifyEmail)
                )
            },
            navigateToNextScreen = {  },
        )
    }

    entry<Screen.Register> {
        RegisterScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToAccountVerificationScreen = {
                navigate(
                    NavigationType.Standard(Screen.OTP(OTPNext.ConfirmAccount))
                )
            }
        )
    }

    entry<Screen.VerifyEmail> {
        VerifyEmailScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToOtpScreen = {
                navigate(
                    NavigationType.Standard(Screen.OTP(OTPNext.ResetPassword))
                )
            }
        )
    }

    entry<Screen.OTP> { key ->
        OtpScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToNextScreen = {
                when (key.next) {
                    OTPNext.ResetPassword -> {
                        navigate(
                            NavigationType.ClearBackStack(Screen.NewPassword)
                        )
                    }

                    OTPNext.ConfirmAccount -> {

                    }
                }
            }
        )
    }

    entry<Screen.NewPassword> {
        NewPasswordScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToLoginScreen = {

            }
        )
    }
}