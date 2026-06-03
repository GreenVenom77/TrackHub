package com.greenvenom.core_navigation.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer

/**
 * Creates a [NavigationState] that survives configuration changes and process death.
 * This state holds the current navigation routes and back stacks for each top-level route.
 *
 * @param startRoute the initial destination when the navigation graph is created
 * @param topLevelRoutes the set of routes that constitute the top-level navigation (e.g., bottom nav items)
 * @return a NavigationState instance that can be hoisted in composables
 */
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {

    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startRoute)
    }

    val mainRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        NavigationState(
            mainRoute = mainRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
    }
}

/**
 * Holds the mutable state of the navigation graph.
 * This class is not intended to be instantiated directly; use [rememberNavigationState] instead.
 *
 * @param mainRoute mutable state representing the route to which the user navigates when exiting the app
 * @param topLevelRoute mutable state representing the currently selected top-level route
 * @param backStacks map of top-level routes to their respective back stacks
 */
class NavigationState(
    mainRoute: MutableState<NavKey>,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by topLevelRoute
    var mainRoute: NavKey by mainRoute

    /**
     * Returns the back stack corresponding to the current [topLevelRoute].
     * Throws an illegal state exception if the stack is missing (should not happen).
     */
    val currentStack: NavBackStack<NavKey>
        get() = backStacks[topLevelRoute] ?:
            error("Stack for $topLevelRoute not found")

    /**
     * The route currently at the top of the current back stack.
     */
    val currentRoute: NavKey
        get() = currentStack.last()

    /**
     * The route just before the current route in the current back stack, if any.
     * Returns null if the current route is the first in its stack.
     */
    val previousRoute: NavKey?
        get() {
            val currentRouteIndex = currentStack.indexOf(currentRoute)
            return if (currentRouteIndex > 0) {
                currentStack[currentRouteIndex - 1]
            } else {
                null
            }
        }

    /**
     * List of top-level routes that currently have active back stacks.
     * If the current top-level route is the main route, only the main route is active.
     * Otherwise, both the main route and the current top-level route are considered active.
     */
    val stacksInUse: List<NavKey>
        get() = if (topLevelRoute == mainRoute) {
            listOf(mainRoute)
        } else {
            listOf(mainRoute, topLevelRoute)
        }
}

/**
 * Converts this [NavigationState] into a list of [NavEntry] instances suitable for consumption by
 * the Navigation 3 library.
 *
 * @param entryProvider a lambda that creates a [NavEntry] given a route key
 * @return a snapshot state list of navigation entries for the active stacks
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {

    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
