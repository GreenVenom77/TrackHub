package com.greenvenom.core_ui.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_ui.components.ErrorDialog
import com.greenvenom.core_ui.components.LoadingDialog
import com.greenvenom.core_ui.components.SuccessDialog
import com.greenvenom.core_ui.components.WarningDialog
import org.koin.androidx.compose.koinViewModel

@Composable
inline fun<reified VM: BaseViewModel> BaseScreen(
    modifier: Modifier = Modifier,
    crossinline onCreateAction: (viewModel: VM) -> Unit = {},
    crossinline onStartAction: (viewModel: VM) -> Unit = {},
    crossinline onResumeAction: (viewModel: VM) -> Unit = {},
    crossinline onPauseAction: (viewModel: VM) -> Unit = {},
    crossinline onStopAction: (viewModel: VM) -> Unit = {},
    crossinline onDestroyAction: (viewModel: VM) -> Unit = {},
    crossinline onPhysicalBack: (viewModel: VM) -> Unit = {},
    enableCustomBack: Boolean = true,
    enableLifecycleObservation: Boolean = false,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    content: @Composable (viewModel: VM) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val viewModel: VM = koinViewModel()
    val baseState by viewModel.baseState.collectAsStateWithLifecycle()

    if (enableLifecycleObservation) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> {
                        onCreateAction(viewModel)
                    }
                    Lifecycle.Event.ON_START -> {
                        onStartAction(viewModel)
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        onResumeAction(viewModel)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        onPauseAction(viewModel)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        onStopAction(viewModel)
                    }
                    Lifecycle.Event.ON_DESTROY -> {
                        onDestroyAction(viewModel)
                    }
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    if (enableCustomBack) {
        BackHandler {
            onPhysicalBack(viewModel)
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) {
        content(viewModel)
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