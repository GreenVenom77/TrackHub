package com.greenvenom.core_navigation.routes

import androidx.navigation3.runtime.NavKey

/**
 * Marker interface for a navigation destination that can be used as a key in Navigation 3.
 * Each destination must provide a [destinationType] to classify its purpose (e.g., main, side, auth).
 */
interface Destination: NavKey {
    val destinationType: DestinationType
}