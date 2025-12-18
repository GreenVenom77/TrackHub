package com.greenvenom.core_ui.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel: ViewModel() {
    private val _baseState = MutableStateFlow(BaseState())
    val baseState: StateFlow<BaseState> = _baseState.asStateFlow()

    fun baseAction(action: BaseAction) {
        when(action) {
            is BaseAction.ShowLoading -> showLoading()
            is BaseAction.HideLoading -> hideLoading()
            is BaseAction.ShowErrorMessage -> showErrorMessage(
                action.errorMessage,
                action.dismissAction
            )
            is BaseAction.ShowWarningDialog -> showWarningDialog(
                action.warningMessage,
                action.dismissAction,
                action.confirmAction,
                action.isDeletionDialog
            )
            is BaseAction.ShowSuccessDialog -> showSuccessDialog(
                action.successTitle,
                action.successMessage,
                action.dismissAction
            )
        }
    }

    fun showLoading() {
        if (_baseState.value.showLoadingDialog) return
        _baseState.update { it.copy(showLoadingDialog = true) }
    }

    fun hideLoading() {
        if (!_baseState.value.showLoadingDialog) return
        _baseState.update { it.copy(showLoadingDialog = false) }
    }

    fun showErrorMessage(givenMessage: String, dismissAction: (() -> Unit)?) {
        _baseState.update {
            it.copy(
                errorMessage = givenMessage,
                onErrorDismiss = dismissAction
            )
        }
    }

    fun hideErrorMessage() {
        if (baseState.value.errorMessage.isEmpty()) return
        _baseState.value.onErrorDismiss?.invoke()
        _baseState.update {
            it.copy(
                errorMessage = "",
                onErrorDismiss = null
            )
        }
    }

    fun showWarningDialog(
        warningMessage: String?,
        dismissAction: (() -> Unit)?,
        confirmAction: (() -> Unit)?,
        isDeletionDialog: Boolean
    ) {
        _baseState.update {
            it.copy(
                showWarningDialog = true,
                warningMessage = warningMessage,
                onWarningDismiss = dismissAction,
                onWarningConfirm = confirmAction,
                isDeletionDialog = isDeletionDialog
            )
        }
    }

    fun hideWarningDialog() {
        if (!_baseState.value.showWarningDialog) return
        _baseState.update {
            it.copy(
                showWarningDialog = false,
                warningMessage = null,
                onWarningDismiss = null,
                onWarningConfirm = null,
                isDeletionDialog = false
            )
        }
    }

    fun showSuccessDialog(
        successTitle: String?,
        successMessage: String?,
        dismissAction: (() -> Unit)?
    ) {
        _baseState.update {
            it.copy(
                showSuccessDialog = true,
                successTitle = successTitle,
                successMessage = successMessage,
                onSuccessDismiss = dismissAction
            )
        }
    }

    fun hideSuccessDialog() {
        if (!_baseState.value.showSuccessDialog) return
        _baseState.update {
            it.copy(
                showSuccessDialog = false,
                successTitle = null,
                successMessage = null,
                onSuccessDismiss = null
            )
        }
    }
}