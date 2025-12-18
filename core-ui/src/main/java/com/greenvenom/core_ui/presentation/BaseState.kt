package com.greenvenom.core_ui.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class BaseState(
    val showLoadingDialog: Boolean = false,
    val errorMessage: String = "",
    val onErrorDismiss: (() -> Unit)? = null,
    val showWarningDialog: Boolean = false,
    val isDeletionDialog: Boolean = false,
    val onWarningConfirm: (() -> Unit)? = null,
    val warningMessage: String? = null,
    val onWarningDismiss: (() -> Unit)? = null,
    val showSuccessDialog: Boolean = false,
    val successTitle: String? = null,
    val successMessage: String? = null,
    val onSuccessDismiss: (() -> Unit)? = null
)