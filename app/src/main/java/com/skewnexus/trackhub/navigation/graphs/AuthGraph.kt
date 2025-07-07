package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_navigation.domain.Destination
import com.greenvenom.feat_auth.presentation.login.LoginScreen
import com.greenvenom.feat_auth.presentation.otp.OtpScreen
import com.greenvenom.feat_auth.presentation.register.RegisterScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.NewPasswordScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.VerifyEmailScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun NavGraphBuilder.authGraph(
    navigate: (NavigationType) -> Unit,
    navigationStateRepository: NavigationStateRepository
) {
    var otpNextScreen: Destination = Screen.Login

    CoroutineScope(Dispatchers.Main).launch {
        navigationStateRepository.navigationState.collect {
            when (it.previousDestination) {
                is Screen.Login -> {
                    otpNextScreen = Screen.Login
                }

                is Screen.Register -> {
                    otpNextScreen = Screen.Login
                }

                is Screen.VerifyEmail -> {
                    otpNextScreen = Screen.NewPassword
                }
            }
        }
    }

    navigation<SubGraph.Auth>(startDestination = Screen.Login) {
        composable<Screen.Login> {
            LoginScreen(
                navigateToRegisterScreen = {
                    navigationStateRepository.navigate(
                        NavigationType.Standard(Screen.Register)
                    )
                },
                navigateToEmailVerificationScreen = {
                    navigationStateRepository.navigate(
                        NavigationType.Standard(Screen.VerifyEmail)
                    )
                },
                navigateToNextScreen = {  },
            )
        }

        composable<Screen.Register> {
            RegisterScreen(
                navigateBack = {
                    navigationStateRepository.navigate(NavigationType.Back)
                },
                navigateToAccountVerificationScreen = {
                    navigationStateRepository.navigate(
                        NavigationType.Standard(Screen.OTP)
                    )
                }
            )
        }

        composable<Screen.VerifyEmail> {
            VerifyEmailScreen(
                navigateBack = {
                    navigationStateRepository.navigate(NavigationType.Back)
                },
                navigateToOtpScreen = {
                    navigationStateRepository.navigate(
                        NavigationType.Standard(Screen.OTP)
                    )
                }
            )
        }

        composable<Screen.OTP> {
            OtpScreen(
                navigateBack = {
                    navigate(NavigationType.Back)
                },
                navigateToNextScreen = {
                    when (otpNextScreen) {
                        is Screen.Login -> {
                            navigate(
                                NavigationType.ClearBackStack(Screen.Login)
                            )
                        }
                        is Screen.Register -> {
                            navigate(
                                NavigationType.ClearBackStack(Screen.Login)
                            )
                        }
                        is Screen.VerifyEmail -> {
                            navigate(
                                NavigationType.ClearBackStack(Screen.NewPassword)
                            )
                        }
                    }
                }
            )
        }

        composable<Screen.NewPassword> {
            NewPasswordScreen(
                navigateBack = {
                    navigationStateRepository.navigate(NavigationType.Back)
                },
                navigateToLoginScreen = {
                    navigationStateRepository.navigate(
                        NavigationType.ClearBackStack(Screen.Login)
                    )
                }
            )
        }
    }
}