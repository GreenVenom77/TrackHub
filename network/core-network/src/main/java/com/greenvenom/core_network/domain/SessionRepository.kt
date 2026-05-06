package com.greenvenom.core_network.domain

import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val userSessionDestination: StateFlow<SessionDestination>

    fun collectSessionStatus()
}