package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_notifications.presentation.screens.NotificationsScreen

fun EntryProviderScope<NavKey>.notificationsGraph(
    navigate: (NavigationType) -> Unit
) {
    entry<Screen.Notifications> {
        NotificationsScreen()
    }
}