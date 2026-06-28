package com.greenvenom.feat_auth.presentation.login

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.buttons.CustomButton
import com.greenvenom.core_ui.components.text.EmailField
import com.greenvenom.core_ui.components.text.PasswordField
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_util.input.domain.ValidationResult
import com.greenvenom.feat_auth.R
import com.greenvenom.feat_auth.presentation.component.AuthHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navigateToRegisterScreen: () -> Unit,
    navigateToEmailVerificationScreen: () -> Unit,
    navigateToNextScreen:() -> Unit,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val baseState by loginViewModel.baseState.collectAsStateWithLifecycle()
    val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

    BaseScreen(
        viewModel = loginViewModel,
        baseState = baseState,
    ) {
        LoginContent(
            state = loginState,
            loginActions = loginViewModel::loginAction,
            baseActions = loginViewModel::baseAction,
            navigateToRegisterScreen = navigateToRegisterScreen,
            navigateToEmailVerificationScreen = navigateToEmailVerificationScreen,
            navigateToNextScreen = navigateToNextScreen
        )
    }
}

@Composable
private fun LoginContent(
    state: LoginState,
    loginActions: (LoginAction) -> Unit,
    baseActions: (BaseAction) -> Unit,
    navigateToRegisterScreen: () -> Unit,
    navigateToEmailVerificationScreen: () -> Unit,
    navigateToNextScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    state.loginNetworkResult
        ?.onSuccess {
            navigateToNextScreen()
        }
        ?.onError {
            baseActions(BaseAction.ShowErrorMessage(
                errorMessage = stringResource(it.messageId)
            ))
            loginActions(LoginAction.ResetNetworkResult)
        }


    DisposableEffect(Unit) {
        onDispose {
            loginActions(LoginAction.ResetState)
            email = ""
            password = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Header Section
        AuthHeader(
            title = stringResource(R.string.sign_in_to_your_account),
            isLoginScreen = true,
            isNavigationBackWanted = false,
            navigateToRegister = navigateToRegisterScreen
        )
        // Input Fields Section
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            EmailField(
                value = email,
                onValueChange = {
                    email = it
                    loginActions(LoginAction.ValidateEmail(email))
                },
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                errorText = if (state.emailValidity is ValidationResult.Error)
                    stringResource(state.emailValidity.error.messageId)
                else ""
            )

            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    loginActions(LoginAction.ValidatePassword(password))
                },
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                errorText = if (state.passwordValidity is ValidationResult.Error)
                    stringResource(state.passwordValidity.error.messageId)
                else ""
            )
            //forgot field
//            Text(
//                stringResource(R.string.forgot_password),
//                color = bluePrimary,
//                modifier = Modifier
//                    .align(Alignment.End)
//                    .clickable(enabled = true) {
//                        navigateToEmailVerificationScreen()
//                    }
//            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomButton(
                text = stringResource(R.string.log_in),
                onClick = {
                    loginActions(LoginAction.Login(email, password))
                },
                enabled = state.emailValidity is ValidationResult.Success
                        && state.passwordValidity is ValidationResult.Success
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginContentsPreview() {
    AppTheme {
        LoginContent(
            state = LoginState(),
            loginActions = { },
            baseActions = { },
            navigateToRegisterScreen = { },
            navigateToEmailVerificationScreen = { },
            navigateToNextScreen = {  },
        )
    }
}
