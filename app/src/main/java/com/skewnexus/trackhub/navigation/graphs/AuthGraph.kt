package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.greenvenom.feat_auth.presentation.login.LoginScreen
import com.greenvenom.feat_auth.presentation.otp.OtpScreen
import com.greenvenom.feat_auth.presentation.register.RegisterScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.NewPasswordScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.VerifyEmailScreen
import com.greenvenom.feat_auth.presentation.routes.AuthDest
import com.greenvenom.feat_auth.presentation.routes.OTPNext

fun EntryProviderScope<NavKey>.authGraph(
    navigate: (NavigationType) -> Unit,
) {
    entry<AuthDest.Login> {
        LoginScreen(
            navigateToRegisterScreen = {
                navigate(
                    NavigationType.Standard(AuthDest.Register)
                )
            },
            navigateToEmailVerificationScreen = {
                navigate(
                    NavigationType.Standard(AuthDest.VerifyEmail)
                )
            },
            navigateToNextScreen = {  },
        )
    }

    entry<AuthDest.Register> {
        RegisterScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToAccountVerificationScreen = {
                navigate(
                    NavigationType.Standard(AuthDest.OTP(OTPNext.ConfirmAccount))
                )
            }
        )
    }

    entry<AuthDest.VerifyEmail> {
        VerifyEmailScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToOtpScreen = {
                navigate(
                    NavigationType.Standard(AuthDest.OTP(OTPNext.ResetPassword))
                )
            }
        )
    }

    entry<AuthDest.OTP> { key ->
        OtpScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToNextScreen = {
                when (key.next) {
                    OTPNext.ResetPassword -> {
                        navigate(
                            NavigationType.ClearBackStack(AuthDest.NewPassword)
                        )
                    }

                    OTPNext.ConfirmAccount -> {

                    }
                }
            }
        )
    }

    entry<AuthDest.NewPassword> {
        NewPasswordScreen(
            navigateBack = {
                navigate(NavigationType.Back)
            },
            navigateToLoginScreen = {

            }
        )
    }
}