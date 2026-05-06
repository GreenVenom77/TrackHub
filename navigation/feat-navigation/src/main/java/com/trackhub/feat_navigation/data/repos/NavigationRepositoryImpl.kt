package com.trackhub.feat_navigation.data.repos

import com.greenvenom.core_navigation.domain.repos.NavigationData
import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.greenvenom.core_navigation.routes.DestinationType
import com.greenvenom.core_navigation.utils.AppNavigator
import com.greenvenom.core_navigation.utils.NavigationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NavigationRepositoryImpl(
    private val appNavigator: AppNavigator
): NavigationRepository {
    private val _navigationData = MutableStateFlow(NavigationData())
    override val navigationData = _navigationData.asStateFlow()

    override fun navigate(navigationType: NavigationType) {
        when(navigationType) {
            is NavigationType.Back -> appNavigator.navigateBack()
            is NavigationType.ClearBackStack -> appNavigator.navigateAndClearBackStack(navigationType.destination)
            is NavigationType.Standard -> appNavigator.navigateTo(navigationType.destination)
        }

        updateStoredDestinations()
    }

    private fun updateStoredDestinations() {
        _navigationData.update {
            it.copy(
                currentDestination = appNavigator.getCurrentDestination(),
                previousDestination = appNavigator.getPreviousDestination()
            )
        }
        updateBarsState()
    }

    private fun updateBarsState() {
        when (_navigationData.value.currentDestination?.destinationType) {
            DestinationType.MAIN -> {
                _navigationData.update {
                    it.copy(
                        isCurrentDestinationSide = false,
                        bottomBarState = true,
                        topBarState = true
                    )
                }
            }
            DestinationType.SIDE -> {
                _navigationData.update {
                    it.copy(
                        isCurrentDestinationSide = true,
                        bottomBarState = false,
                        topBarState = true
                    )
                }
            }
            else -> {
                _navigationData.update {
                    it.copy(
                        isCurrentDestinationSide = false,
                        bottomBarState = false,
                        topBarState = false
                    )
                }
            }
        }
    }
}