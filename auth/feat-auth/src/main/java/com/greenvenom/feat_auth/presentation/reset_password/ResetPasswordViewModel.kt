package com.greenvenom.feat_auth.presentation.reset_password

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_auth.data.repository.EmailStateRepository
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.core_util.input.InputValidator
import com.greenvenom.core_util.input.domain.onError
import com.greenvenom.core_util.input.domain.onSuccess
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val emailStateRepository: EmailStateRepository,
    private val authRepository: AuthRepository
): BaseViewModel() {
    private val _resetPasswordState = MutableStateFlow(ResetPasswordState())
    val resetPasswordState = _resetPasswordState.asStateFlow()

    fun resetPasswordAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.UpdateEmail -> {
                validateEmail(action.email)
            }
            is ResetPasswordAction.ValidatePassword -> {
                _resetPasswordState.update {
                    it.copy(
                        passwordValidity = InputValidator.validatePassword(action.password)
                    )
                }
            }
            is ResetPasswordAction.ValidatePasswordConfirmation -> {
                _resetPasswordState.update {
                    it.copy(
                        confirmPasswordValidity = InputValidator.validatePasswordConfirmation(
                            password = action.password,
                            confirmPassword = action.confirmPassword
                        )
                    )
                }
            }
            is ResetPasswordAction.SendResetPasswordEmail -> {
                emailStateRepository.emailState.value.email?.let {
                    sendPasswordResetEmail(
                        email = it
                    )
                }
            }
            is ResetPasswordAction.UpdatePassword -> {
                updatePassword(action.newPassword)
            }
            is ResetPasswordAction.ResetState -> resetState()
            is ResetPasswordAction.ResetEmailResult -> resetEmailResult()
            is ResetPasswordAction.ResetPasswordResult -> resetPasswordResult()
        }
    }

    private fun validateEmail(email: String) {
        val typedEmailValidity = InputValidator.validateEmail(email)
        typedEmailValidity.onError { emailStateRepository.updateEmailValidity(typedEmailValidity) }
        typedEmailValidity.onSuccess {
            emailStateRepository.updateEmail(email)
            emailStateRepository.updateEmailValidity(typedEmailValidity)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val result = authRepository.sendResetPasswordEmail(
                email = email
            )
            _resetPasswordState.update { it.copy(emailSentNetworkResult = result) }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val result = authRepository.updatePassword(
                newPassword = newPassword
            )
            _resetPasswordState.update { it.copy(passwordUpdatedNetworkResult = result) }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun resetState() {
        _resetPasswordState.update { ResetPasswordState() }
    }

    private fun resetEmailResult() {
        _resetPasswordState.update { it.copy(emailSentNetworkResult = null) }
    }

    private fun resetPasswordResult() {
        _resetPasswordState.update { it.copy(passwordUpdatedNetworkResult = null) }
    }
}