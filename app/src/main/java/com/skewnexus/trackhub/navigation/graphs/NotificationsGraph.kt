package com.skewnexus.trackhub.navigation.graphs

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun NavGraphBuilder.notificationsGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.Notifications>(startDestination = Screen.Activity) {
        composable<Screen.Activity> {
            Text(text = "Activity")
        }
    }
}