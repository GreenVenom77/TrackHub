package com.greenvenom.core_ui.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalInspectionMode
import com.greenvenom.core_ui.presentation.ScaffoldViewModel

val LocalScaffoldViewModel = compositionLocalOf<ScaffoldViewModel> {
    error("ScaffoldViewModel not provided")
}

@Composable
fun SetScaffold(
    title: String = "",
    showLogo: Boolean = true,
    navigateBackAction: (() -> Unit)? = null,
    topBarActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
) {
    val isInPreview = LocalInspectionMode.current

    if (!isInPreview) {
        // Only access LocalScaffoldViewModel when not in preview
        val scaffoldViewModel = LocalScaffoldViewModel.current

        val stableTopBarActions by rememberUpdatedState(topBarActions)
        val stableFloatingActionButton by rememberUpdatedState(floatingActionButton)

        LaunchedEffect(title, navigateBackAction, stableTopBarActions, stableFloatingActionButton) {
            scaffoldViewModel.updateScaffold(
                title = title,
                showLogo = showLogo,
                navigateBackAction = navigateBackAction,
                topBarActions = stableTopBarActions,
                floatingActionButton = stableFloatingActionButton,
            )
        }
    }
}