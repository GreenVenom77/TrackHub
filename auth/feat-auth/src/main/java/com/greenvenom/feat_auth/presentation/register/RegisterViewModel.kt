package com.greenvenom.feat_auth.presentation.register

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_auth.data.repository.EmailStateRepository
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.core_util.input.InputValidator
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val emailStateRepository: EmailStateRepository,
    private val authRepository: AuthRepository
): BaseViewModel() {
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    fun registerAction(action: RegisterAction) {
        when(action) {
            is RegisterAction.ValidateUsername -> {
                _registerState.update {
                    it.copy(
                        usernameValidity = InputValidator.validateName(action.username)
                    )
                }
            }
            is RegisterAction.ValidateEmail -> {
                _registerState.update {
                    it.copy(
                        emailValidity = InputValidator.validateEmail(action.email)
                    )
                }
            }
            is RegisterAction.ValidatePassword -> {
                _registerState.update {
                    it.copy(
                        passwordValidity = InputValidator.validatePassword(action.password)
                    )
                }
            }
            is RegisterAction.ValidatePasswordConfirmation -> {
                _registerState.update {
                    it.copy(
                        confirmPasswordValidity = InputValidator.validatePasswordConfirmation(
                            password = action.password,
                            confirmPassword = action.confirmPassword
                        )
                    )
                }
            }
            is RegisterAction.Register -> registerUser(
                username = action.username,
                email = action.email,
                password = action.password,
            )
            is RegisterAction.ResetState -> resetState()
            is RegisterAction.ResetNetworkResult -> resetNetworkResult()
        }
    }

    private fun registerUser(
        username: String,
        email: String,
        password: String,
    ) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val result = authRepository.registerUser(
                email = email,
                password = password,
                fullName = username
            )
            result.onSuccess { emailStateRepository.updateEmail(email) }
            _registerState.update { it.copy(registrationNetworkResult = result) }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun resetState() {
        _registerState.update { RegisterState() }
    }

    private fun resetNetworkResult() {
        _registerState.update { it.copy(registrationNetworkResult = null) }
    }
}