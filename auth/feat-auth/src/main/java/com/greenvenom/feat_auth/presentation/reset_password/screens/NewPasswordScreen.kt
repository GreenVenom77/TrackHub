package com.greenvenom.feat_auth.presentation.reset_password.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.greenvenom.feat_auth.R
import com.greenvenom.core_ui.components.CustomButton
import com.greenvenom.feat_auth.presentation.component.AuthHeader
import com.greenvenom.core_ui.components.CustomTextField
import com.greenvenom.feat_auth.presentation.reset_password.ResetPasswordAction
import com.greenvenom.feat_auth.presentation.reset_password.ResetPasswordState
import com.greenvenom.feat_auth.presentation.reset_password.ResetPasswordViewModel
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.validation.domain.ValidationResult

@Composable
fun NewPasswordScreen(
    navigateBack: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    BaseScreen<ResetPasswordViewModel>(
        onPhysicalBack = { navigateBack() }
    ) { viewModel ->
        val resetPasswordState by viewModel.resetPasswordState.collectAsStateWithLifecycle()

        NewPasswordContent(
            state = resetPasswordState,
            resetPasswordActions = viewModel::resetPasswordAction,
            baseActions = viewModel::baseAction,
            navigateToLoginScreen = navigateToLoginScreen,
            navigateBack = navigateBack
        )
    }
}

@Composable
private fun NewPasswordContent(
    state: ResetPasswordState,
    resetPasswordActions: (ResetPasswordAction) -> Unit,
    baseActions: (BaseAction) -> Unit,
    navigateToLoginScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    state.passwordUpdatedNetworkResult
        ?.onSuccess {
            baseActions(BaseAction.HideLoading)
            navigateToLoginScreen()
        }
        ?.onError {
            baseActions(BaseAction.HideLoading)
            baseActions(BaseAction.ShowErrorMessage(
                errorMessage = stringResource(it.messageId)
            ))
            resetPasswordActions(ResetPasswordAction.ResetPasswordResult)
        }

    DisposableEffect(Unit) {
        onDispose {
            resetPasswordActions(ResetPasswordAction.ResetState)
            password = ""
            confirmPassword = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AuthHeader(
            title = stringResource(R.string.create_new_password),
            navigateBack = navigateBack,
            isLoginScreen = false,
            isNavigationBackWanted = true
        )
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(13.dp) // Adds spacing between items
        ) {
            Text(
                text = stringResource(R.string.Password),
                color = MaterialTheme.colorScheme.onBackground
            )
            CustomTextField(
                value = password,
                onValueChange = {
                    password = it
                    resetPasswordActions(ResetPasswordAction.ValidatePassword(password))
                },
                label = stringResource(R.string.enter_your_password),
                error = if (state.passwordValidity is ValidationResult.Error)
                    stringResource(state.passwordValidity.error.messageId)
                else "",
                isPasswordField = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            // Confirm Password Field
            Text(
                text = stringResource(R.string.confirm_password),
                color = MaterialTheme.colorScheme.onBackground
            )
            CustomTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    resetPasswordActions(ResetPasswordAction.ValidatePasswordConfirmation(
                        password, confirmPassword
                    ))
                },
                label = stringResource(R.string.confirm_your_password),
                error = if (state.confirmPasswordValidity is ValidationResult.Error)
                    stringResource(state.confirmPasswordValidity.error.messageId)
                else "",
                isPasswordField = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomButton(
                text = stringResource(R.string.confirm),
                enabled = state.passwordValidity is ValidationResult.Success
                        && state.confirmPasswordValidity is ValidationResult.Success,
                onClick = {
                    baseActions(BaseAction.ShowLoading)
                    resetPasswordActions(ResetPasswordAction.UpdatePassword(password))
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun NewPasswordContentPreview() {
    AppTheme {
        NewPasswordContent(
            state = ResetPasswordState(),
            resetPasswordActions = { },
            baseActions = { },
            navigateToLoginScreen = { },
            navigateBack = { }
        )
    }
}