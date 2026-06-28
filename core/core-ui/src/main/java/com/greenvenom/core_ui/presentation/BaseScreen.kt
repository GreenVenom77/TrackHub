package com.greenvenom.core_ui.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.greenvenom.core_ui.components.dialogs.ErrorDialog
import com.greenvenom.core_ui.components.dialogs.LoadingDialog
import com.greenvenom.core_ui.components.dialogs.SuccessDialog
import com.greenvenom.core_ui.components.dialogs.WarningDialog

@Composable
inline fun BaseScreen(
    viewModel: BaseViewModel,
    baseState: BaseState,
    modifier: Modifier = Modifier,
    crossinline onPhysicalBack: () -> Unit = {},
    enableCustomPhysicalBack: Boolean = false,
    content: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current

    if (enableCustomPhysicalBack) {
        BackHandler {
            onPhysicalBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        content()
        LoadingDialog(
            isLoading = baseState.showLoadingDialog
        )
        ErrorDialog(
            errorMessage = baseState.errorMessage,
            dismissAction = { viewModel.hideErrorMessage() }
        )
        SuccessDialog(
            showDialog = baseState.showSuccessDialog,
            successTitle = baseState.successTitle,
            successMessage = baseState.successMessage,
            onDismiss = { viewModel.hideSuccessDialog() }
        )
        WarningDialog(
            showDialog = baseState.showWarningDialog,
            warningMessage = baseState.warningMessage,
            onDismiss = { viewModel.hideWarningDialog() },
            onConfirm = { baseState.onWarningConfirm?.invoke() },
            isDeletion = baseState.isDeletionDialog
        )
    }
}