package com.greenvenom.core_navigation.domain.repos

import com.greenvenom.core_navigation.domain.NavigationState
import com.greenvenom.core_navigation.utils.NavigationType
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for handling navigation events and exposing navigation state.
 * Implementations of this interface are responsible for mutating the navigation state
 * (e.g., updating current/previous destinations, toggling top/bottom bar visibility)
 * in response to navigation actions.
 */
interface NavigationRepository {
    /** Flow emitting the current navigation data, including current and previous destinations,
     * bar visibility flags, and account type index. UI layers should collect this flow
     * to react to navigation state changes.
     */
    val navigationData: StateFlow<NavigationData>

    /**
     * Binds the navigator to a mutable [NavigationState] instance.
     * This must be called before any navigation operations.
     *
     * @param navigationState the state to mutate
     */
    fun bind(navigationState: NavigationState)

    /**
     * Triggers a navigation action based on the provided [navigationType].
     *
     * When this method is called, the implementing class (NavigationRepositoryImpl) executes
     * the following sequence of operations:
     *
     * 1. **Dispatches to AppNavigator**: The specific navigation type determines which
     *    AppNavigator action is executed:
     *    - [NavigationType.Back]: Calls navigateBack() to return to previous destinations
     *    - [NavigationType.ClearBackStack]: Calls navigateAndClearBackStack(destination)
     *      to navigate and clear the back stack after reaching the destination
     *    - [NavigationType.Standard]: Calls navigateTo(destination) for standard navigation
     *
     * 2. **Updates Current/Previous Destinations**: After navigation completes, retrieves
     *    the current and previous destinations from AppNavigator via:
     *    - getCurrentDestination()
     *    - getPreviousDestination()
     *    These values are stored in NavigationData to track navigation history.
     *
     * 3. **Updates Bar Visibility States**: Based on the destination type (MAIN, SIDE, or
     *    other), updates the top and bottom bar visibility flags:
     *    - MAIN destinations: isCurrentDestinationSide=false, bottomBarState=true,
     *      topBarState=true
     *    - SIDE destinations: isCurrentDestinationSide=true, bottomBarState=false,
     *      topBarState=true  
     *    - Other destinations: isCurrentDestinationSide=false, bottomBarState=false,
     *      topBarState=false
     *
     * The updated navigation data (including current/previous destinations and bar states)
     * flows through the StateFlow to UI components that react to these changes.
     *
     * @param navigationType the type of navigation to perform:
     *        [NavigationType.Back] - Navigate back one level in the stack
     *        [NavigationType.ClearBackStack] - Navigate and clear all back stacks after reaching destination
     *        [NavigationType.Standard] - Standard push navigation without clearing back stack
     */
    fun navigate(navigationType: NavigationType)
}