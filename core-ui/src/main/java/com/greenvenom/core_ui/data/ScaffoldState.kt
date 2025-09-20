package com.greenvenom.core_ui.data

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class ScaffoldState(
    val title: String = "",
    val navigateBackAction: (() -> Unit)? = null,
    val topBarActions: @Composable RowScope.() -> Unit = {},
    val floatingActionButton: @Composable () -> Unit = {},
)
