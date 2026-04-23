package com.greenvenom.core_navigation.routes

import androidx.navigation3.runtime.NavKey

interface Destination: NavKey {
    val destinationType: DestinationType
}