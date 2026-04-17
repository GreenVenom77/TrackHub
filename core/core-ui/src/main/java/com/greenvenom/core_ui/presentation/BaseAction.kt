package com.greenvenom.core_ui.presentation

sealed interface BaseAction {
    data object ShowLoading: BaseAction
    data object HideLoading: BaseAction
    data class ShowErrorMessage(
        val errorMessage: String,
        val dismissAction: (() -> Unit)? = null
    ): BaseAction
    data class ShowWarningDialog(
        val warningMessage: String? = null,
        val dismissAction: (() -> Unit)? = null,
        val confirmAction: (() -> Unit)? = null,
        val isDeletionDialog: Boolean = false
    ): BaseAction
    data class ShowSuccessDialog(
        val successTitle: String? = null,
        val successMessage: String? = null,
        val dismissAction: (() -> Unit)? = null
    ): BaseAction
}