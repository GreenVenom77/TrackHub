package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph
import com.trackhub.feat_notifications.presentation.screens.NotificationsScreen

fun NavGraphBuilder.notificationsGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.Notifications>(startDestination = Screen.Notifications) {
        composable<Screen.Notifications> {
            NotificationsScreen(
                navigateBack = { navigate(NavigationType.Back) }
            )
        }
    }
}