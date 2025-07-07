package com.skewnexus.trackhub.navigation.graphs

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.greenvenom.core_navigation.data.NavigationType
import com.greenvenom.feat_menu.presentation.MenuScreen
import com.trackhub.feat_navigation.routes.Screen
import com.trackhub.feat_navigation.routes.SubGraph

fun NavGraphBuilder.menuGraph(
    navigate: (NavigationType) -> Unit
) {
    navigation<SubGraph.Menu>(startDestination = Screen.Menu) {
        composable<Screen.Menu> {
            MenuScreen(
                navigateToProfile = {
                    navigate(
                        NavigationType.Standard(Screen.Profile)
                    )
                },
                navigateBack = { navigate(NavigationType.Back) },
            )
        }

        composable<Screen.Profile> {
            Text(text = "Profile")
        }
    }
}