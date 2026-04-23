package com.greenvenom.core_navigation.domain.repos

import com.greenvenom.core_navigation.utils.NavigationType
import kotlinx.coroutines.flow.StateFlow

interface NavigationStateRepository {
    val navigationState: StateFlow<NavigationState>

    fun navigate(navigationType: NavigationType)
}