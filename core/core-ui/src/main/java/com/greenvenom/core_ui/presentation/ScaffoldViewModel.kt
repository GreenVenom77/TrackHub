package com.greenvenom.core_ui.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.greenvenom.core_ui.data.ScaffoldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScaffoldViewModel: ViewModel() {
    private val _scaffoldState = MutableStateFlow(ScaffoldState())
    val scaffoldState: StateFlow<ScaffoldState> = _scaffoldState.asStateFlow()

    fun updateScaffold(
        title: String = "",
        showLogo: Boolean = true,
        showSearchBar: Boolean = false,
        onSearchQueryChange: (String) -> Unit = {},
        navigateBackAction: (() -> Unit)? = null,
        topBarActions: @Composable RowScope.() -> Unit = {},
        floatingActionButton: @Composable () -> Unit = {},
    ) {
        _scaffoldState.value = ScaffoldState(
            title = title,
            showLogo = showLogo,
            showSearchBar = showSearchBar,
            onSearchQueryChange = onSearchQueryChange,
            navigateBackAction = navigateBackAction,
            topBarActions = topBarActions,
            floatingActionButton = floatingActionButton,
        )
    }
}