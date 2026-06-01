package com.skewnexus.trackhub.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_menu.presentation.MenuScreen
import com.trackhub.feat_menu.presentation.routes.MenuDest

fun EntryProviderScope<NavKey>.menuGraph(
    navigate: (NavigationType) -> Unit
) {
    entry<MenuDest.Menu> {
        MenuScreen(
            navigateToProfile = {  }
        )
    }
}