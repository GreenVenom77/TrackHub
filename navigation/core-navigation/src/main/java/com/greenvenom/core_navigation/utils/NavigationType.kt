package com.greenvenom.core_navigation.utils

import com.greenvenom.core_navigation.routes.Destination

/**
 * Represents a navigation action that can be performed.
 * Used by the [NavigationRepository] to trigger navigation via the [com.trackhub.feat_navigation.data.AppNavigator].
 */
sealed class NavigationType {
    /** Navigate back to the previous destination in the current back stack. */
    data object Back : NavigationType()
    /**
     * Navigate to a destination while clearing the back stack of all top-level routes.
     * @param destination the destination to navigate to
     */
    data class ClearBackStack(val destination: Destination) : NavigationType()
    /**
     * Standard navigation to a destination, preserving the current back stack.
     * @param destination the destination to navigate to
     */
    data class Standard(val destination: Destination) : NavigationType()
}