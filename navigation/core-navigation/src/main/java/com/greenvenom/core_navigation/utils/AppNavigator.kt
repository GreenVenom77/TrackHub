package com.greenvenom.core_navigation.utils

import com.greenvenom.core_navigation.domain.NavigationState
import com.greenvenom.core_navigation.routes.Destination

/**
 * Interface defining the core navigation operations used by the repository and UI layers.
 * Implementations should mutate the provided [NavigationState] to reflect navigation changes.
 */
interface AppNavigator {
    /**
     * Binds the navigator to a mutable [NavigationState] instance.
     * This must be called before any navigation operations.
     *
     * @param navigationState the state to mutate
     */
    fun bind(navigationState: NavigationState)

    /**
     * Navigate to the specified destination using a standard push operation.
     *
     * @param target the destination to navigate to
     */
    fun navigateTo(target: Destination)

    /**
     * Navigate back to the previous destination in the current back stack.
     * If already at the root, the behavior is defined by the implementing class
     * (typically, it will navigate to the main route or do nothing).
     */
    fun navigateBack()

    /**
     * Navigate to the specified destination while clearing the back stack of all top-level routes.
     *
     * @param target the destination to navigate to
     */
    fun navigateAndClearBackStack(target: Destination)

    /**
     * Get the currently displayed destination.
     *
     * @return the current destination
     */
    fun getCurrentDestination(): Destination

    /**
     * Get the destination shown before the current one, if any.
     *
     * @return the previous destination, or null if at the start of a stack
     */
    fun getPreviousDestination(): Destination?
}