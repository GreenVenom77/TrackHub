package com.greenvenom.core_navigation.domain.repos

import com.greenvenom.core_navigation.utils.NavigationType
import kotlinx.coroutines.flow.StateFlow

interface NavigationRepository {
    val navigationData: StateFlow<NavigationData>

    fun navigate(navigationType: NavigationType)
}