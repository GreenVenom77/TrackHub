package com.greenvenom.feat_menu.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_ui.components.LanguageSwitcher
import com.greenvenom.core_ui.components.ThemeSwitcher
import com.greenvenom.core_ui.utils.SetScaffold
import com.greenvenom.feat_menu.presentation.components.MenuCard
import com.trackhub.feat_menu.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun MenuScreen(
    navigateToProfile: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MenuViewModel = koinViewModel()
    val menuState by viewModel.menuState.collectAsStateWithLifecycle()

    MenuContent(
        menuState = menuState,
        menuAction = { action ->
            when (action) {
                is MenuAction.NavigateToProfile -> navigateToProfile()
            }
            viewModel.menuAction(action)
        },
        modifier = modifier
    )
}

@Composable
private fun MenuContent(
    menuState: MenuState,
    menuAction: (MenuAction) -> Unit,
    modifier: Modifier = Modifier
) {
    SetScaffold()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ThemeSwitcher(
            darkTheme = menuState.isDarkTheme ?: isSystemInDarkTheme(),
            onClick = { isDarkTheme -> menuAction(MenuAction.ChangeTheme(isDarkTheme)) },
            size = 75.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        LanguageSwitcher(
            isArabic = menuState.isArabic,
            onClick = { menuAction(MenuAction.ChangeLanguage(it)) },
            size = 75.dp
        )
        Spacer(modifier = Modifier.weight(1f))
        MenuCard(
            title = stringResource(R.string.logout),
            onClick = {
                menuAction(MenuAction.Logout)
            },
            painter = painterResource(R.drawable.exit_ic),
            iconDescription = stringResource(R.string.logout_icon),
            titleStyle = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            contentColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MenuContentPreview() {
    MenuContent(
        menuState = MenuState(),
        menuAction = {},
    )
}