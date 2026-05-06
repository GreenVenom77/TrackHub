package com.skewnexus.trackhub.navigation.utils

import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.greenvenom.core_navigation.utils.NavigationType
import com.greenvenom.core_network.domain.SessionDestination
import com.greenvenom.core_network.domain.SessionRepository
import com.trackhub.feat_navigation.routes.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionDestinationHandler(
    private val navigationRepository: NavigationRepository,
    private val sessionStateRepository: SessionRepository
) {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    fun collectSessionDestinations() {
        CoroutineScope(Dispatchers.Main).launch {
            sessionStateRepository.userSessionDestination.collectLatest { wantedDestination ->
                _isReady.update {
                    wantedDestination != SessionDestination.INITIALIZE
                }
                handleSessionStates(wantedDestination)
            }
        }
    }

    private fun handleSessionStates(wantedDestination: SessionDestination) {
        when (wantedDestination) {
            SessionDestination.INITIALIZE -> {

            }

            SessionDestination.AUTH -> {
                navigationRepository.navigate(
                    NavigationType.ClearBackStack(Screen.Login)
                )
            }

            SessionDestination.MAIN -> {
                navigationRepository.navigate(
                    NavigationType.ClearBackStack(Screen.OwnedHubs())
                )
            }
        }
    }
}