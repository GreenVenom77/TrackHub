package com.greenvenom.feat_auth.presentation.login

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_auth.data.dto.request.LoginRequest
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.validation.ValidateInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
): BaseViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun loginAction(action: LoginAction) {
        when(action) {
            is LoginAction.ValidateEmail -> {
                _loginState.update {
                    it.copy(
                        emailValidity = ValidateInput.validateEmail(action.email)
                    )
                }
            }
            is LoginAction.ValidatePassword -> {
                _loginState.update {
                    it.copy(
                        passwordValidity = ValidateInput.validateLoginPassword(action.password)
                    )
                }
            }
            is LoginAction.Login -> loginUser(
                email = action.email,
                password = action.password
            )
            is LoginAction.ResetState -> resetState()
            is LoginAction.ResetNetworkResult -> resetNetworkResult()
        }
    }

    private fun loginUser(
        email: String,
        password: String,
    ) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val result = authRepository.loginUser(
                LoginRequest(email, password)
            )
            _loginState.update { it.copy(loginNetworkResult = result) }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun resetState() {
        _loginState.update { LoginState() }
    }

    private fun resetNetworkResult() {
        _loginState.update { it.copy(loginNetworkResult = null) }
    }
}
