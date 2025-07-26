package com.greenvenom.core_ui.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.greenvenom.core_ui.presentation.ScaffoldViewModel

val LocalScaffoldViewModel = compositionLocalOf<ScaffoldViewModel> {
    error("ScaffoldViewModel not provided")
}

@Composable
fun SetScaffold(
    title: String = "",
    navigateBackAction: (() -> Unit)? = null,
    topBarActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
) {
    val scaffoldViewModel = LocalScaffoldViewModel.current

    val stableTopBarActions by rememberUpdatedState(topBarActions)

    val stableFloatingActionButton by rememberUpdatedState(floatingActionButton)

    LaunchedEffect(title, navigateBackAction, stableTopBarActions, stableFloatingActionButton) {
        scaffoldViewModel.updateScaffold(
            title = title,
            navigateBackAction = navigateBackAction,
            topBarActions = stableTopBarActions,
            floatingActionButton = stableFloatingActionButton,
        )
    }
}