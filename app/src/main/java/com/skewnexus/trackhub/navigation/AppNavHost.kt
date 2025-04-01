package com.skewnexus.trackhub.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.greenvenom.core_navigation.data.NavigationType
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_navigation.utils.AppNavigator
import com.trackhub.feat_navigation.data.DestinationHandler
import com.trackhub.feat_hub.presentation.hub_details.screens.HubDetailsScreen
import com.trackhub.feat_hub.presentation.hub_list.HubListScreen
import com.greenvenom.feat_auth.presentation.login.LoginScreen
import com.greenvenom.feat_auth.presentation.otp.OtpScreen
import com.greenvenom.feat_auth.presentation.register.RegisterScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.NewPasswordScreen
import com.greenvenom.feat_auth.presentation.reset_password.screens.VerifyEmailScreen
import com.greenvenom.feat_auth.presentation.splash.SplashScreen
import com.greenvenom.feat_menu.presentation.MenuScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph
import kotlinx.coroutines.flow.update
import org.koin.compose.koinInject

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val appNavigator = koinInject<AppNavigator>()
    val navigationStateRepository = koinInject<NavigationStateRepository>()
    val navigationState by navigationStateRepository.navigationState.collectAsStateWithLifecycle()
    val destinationHandler = koinInject<DestinationHandler>()
    val destinationState by destinationHandler.destinationState.collectAsStateWithLifecycle()

    appNavigator.config(
        returnedDestination = Screen::class,
        navController = rememberNavController()
    )

    NavHost(
        navController = appNavigator.navController,
        startDestination = Screen.Splash,
        modifier = modifier
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onStart = {
                    destinationHandler.collectSessionDestinations()
                }
            )
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
                        navigationStateRepository.navigate(NavigationType.Back)
                    },
                    navigateToNewPasswordScreen = {
                        if (navigationState.previousDestination is Screen.VerifyEmail) {
                            navigationStateRepository.navigate(
                                NavigationType.ClearBackStack(Screen.NewPassword)
                            )
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

        navigation<SubGraph.Main>(startDestination = Screen.MyHubs) {
            composable<Screen.MyHubs> {
                HubListScreen(
                    showOwnedHubs = true,
                    navigateToHubDetails = { hubId ->
                        navigationStateRepository.navigate(
                            NavigationType.Standard(Screen.HubDetails(hubId))
                        )
                    },
                    onPhysicalBack = { navigationStateRepository.updateStoredDestinations() },
                    hubBottomSheetState = destinationState.hubBottomSheetState,
                    onSheetDismiss = {
                        destinationHandler.destinationState.update { state ->
                            state.copy(
                                hubBottomSheetState = false,
                                itemBottomSheetState = false
                            )
                        }
                    }
                )
            }
            composable<Screen.SharedHubs> {
                HubListScreen(
                    showOwnedHubs = false,
                    navigateToHubDetails = { hubId ->
                        navigationStateRepository.navigate(
                            NavigationType.Standard(Screen.HubDetails(hubId))
                        )
                    },
                    onPhysicalBack = { navigationStateRepository.updateStoredDestinations() },
                    hubBottomSheetState = destinationState.hubBottomSheetState,
                    onSheetDismiss = {
                        destinationHandler.destinationState.update { state ->
                            state.copy(
                                hubBottomSheetState = false,
                                itemBottomSheetState = false
                            )
                        }
                    }
                )
            }
            composable<Screen.HubDetails> {
                val args = it.toRoute<Screen.HubDetails>()

                HubDetailsScreen(
                    hubId = args.hubId,
                    onPhysicalBack = { navigationStateRepository.updateStoredDestinations() },
                    onHubDeletion = {
                        navigationStateRepository.navigate(NavigationType.Back)
                        destinationHandler.destinationState.update { state ->
                            state.copy(
                                currentHub = null,
                                hubBottomSheetState = false
                            )
                        }
                    },
                    onHubRetrieval = { hub ->
                        destinationHandler.destinationState.update { state ->
                            state.copy(currentHub = hub)
                        }
                    },
                    hubBottomSheetState = destinationState.hubBottomSheetState,
                    itemBottomSheetState = destinationState.itemBottomSheetState,
                    onSheetDismiss = {
                        destinationHandler.destinationState.update { state ->
                            state.copy(
                                hubBottomSheetState = false,
                                itemBottomSheetState = false
                            )
                        }
                    },
                    onEditItem = {
                        destinationHandler.destinationState.update { state ->
                            state.copy(
                                hubBottomSheetState = false,
                                itemBottomSheetState = true
                            )
                        }
                    },
                    navigateBack = {
                        navigationStateRepository.navigate(NavigationType.Back)
                    }
                )
            }
            composable<Screen.Activity> {
                Text(text = "Activity")
            }
            composable<Screen.Menu> {
                MenuScreen(
                    navigateToProfile = {
                        navigationStateRepository.navigate(
                            NavigationType.Standard(Screen.Profile)
                        )
                    },
                    onPhysicalBack = { navigationStateRepository.updateStoredDestinations() },
                )
            }
            composable<Screen.Profile>(
                enterTransition = { slideInHorizontally { it } + fadeIn() },
                popExitTransition = { slideOutHorizontally { it } + fadeOut() }
            ) {
                Text(text = "Profile")
            }
        }
    }
}