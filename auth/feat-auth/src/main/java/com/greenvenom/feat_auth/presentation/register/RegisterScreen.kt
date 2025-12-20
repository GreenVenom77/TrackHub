package com.greenvenom.feat_auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.buttons.CustomButton
import com.greenvenom.core_ui.components.text.CustomTextField
import com.greenvenom.core_ui.components.text.EmailField
import com.greenvenom.core_ui.components.text.PasswordField
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.feat_auth.R
import com.greenvenom.feat_auth.presentation.component.AuthHeader
import com.greenvenom.validation.domain.ValidationResult

@Composable
fun RegisterScreen(
    navigateBack: () -> Unit,
    navigateToAccountVerificationScreen: () -> Unit,
) {
    BaseScreen<RegisterViewModel>(
        onPhysicalBack = { navigateBack() }
    ) { viewModel ->
        val state by viewModel.registerState.collectAsStateWithLifecycle()

        RegisterContent(
            state = state,
            registerActions = viewModel::registerAction,
            baseActions = viewModel::baseAction,
            navigateBack = navigateBack,
            navigateToAccountVerificationScreen = navigateToAccountVerificationScreen
        )
    }
}

@Composable
private fun RegisterContent(
    state: RegisterState,
    registerActions: (RegisterAction) -> Unit,
    baseActions: (BaseAction) -> Unit,
    navigateBack: () -> Unit,
    navigateToAccountVerificationScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    state.registrationNetworkResult
        ?.onSuccess {
            navigateToAccountVerificationScreen()
        }
        ?.onError {
            baseActions(BaseAction.ShowErrorMessage(
                errorMessage = stringResource(it.messageId)
            ))
            registerActions(RegisterAction.ResetNetworkResult)
        }

    DisposableEffect(Unit) {
        onDispose {
            registerActions(RegisterAction.ResetState)
            username = ""
            email = ""
            password = ""
            confirmPassword = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Header Section
        AuthHeader(
            title = stringResource(R.string.register),
            isLoginScreen = false,
            navigateBack = navigateBack
        )
        // Input Fields Section
        Column(
            verticalArrangement = Arrangement.spacedBy(13.dp),
            modifier = Modifier
                .padding(18.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CustomTextField(
                value = username,
                onValueChange = {
                    username = it
                    registerActions(RegisterAction.ValidateUsername(username))
                },
                label = stringResource(R.string.enter_your_username),
                errorText = if (state.usernameValidity is ValidationResult.Error)
                    stringResource(state.usernameValidity.error.messageId)
                else "",
                isPasswordField = false,
                imeAction = ImeAction.Next
            )

            EmailField(
                value = email,
                onValueChange = {
                    email = it
                    registerActions(RegisterAction.ValidateEmail(email))
                },
                label = stringResource(R.string.enter_your_email),
                errorText = if (state.emailValidity is ValidationResult.Error)
                    stringResource(state.emailValidity.error.messageId)
                else "",
                imeAction = ImeAction.Next
            )

            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    registerActions(RegisterAction.ValidatePassword(password))
                },
                label = stringResource(R.string.enter_your_password),
                errorText = if (state.passwordValidity is ValidationResult.Error)
                    stringResource(state.passwordValidity.error.messageId)
                else "",
                imeAction = ImeAction.Next
            )

            PasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    registerActions(RegisterAction.ValidatePasswordConfirmation(password, confirmPassword))
                },
                label = stringResource(R.string.confirm_your_password),
                errorText = if (state.confirmPasswordValidity is ValidationResult.Error)
                    stringResource(state.confirmPasswordValidity.error.messageId)
                else ""
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Register Button
            CustomButton(
                text = stringResource(R.string.register),
                onClick = {
                    registerActions(
                        RegisterAction.Register(
                            username,
                            email,
                            password,
                        )
                    )
                },
                enabled = state.usernameValidity is ValidationResult.Success &&
                        state.emailValidity is ValidationResult.Success &&
                        state.passwordValidity is ValidationResult.Success &&
                        state.confirmPasswordValidity is ValidationResult.Success
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun RegisterContentsPreview() {
    AppTheme {
        RegisterContent(
            state = RegisterState(),
            registerActions = { },
            baseActions = { },
            navigateBack = { },
            navigateToAccountVerificationScreen = { }
        )
    }
}