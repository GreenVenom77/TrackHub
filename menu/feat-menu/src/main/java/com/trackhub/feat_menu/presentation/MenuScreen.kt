package com.trackhub.feat_menu.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_ui.components.buttons.ButtonVariant
import com.greenvenom.core_ui.components.buttons.CustomButton
import com.greenvenom.core_ui.components.buttons.LanguageSwitcher
import com.greenvenom.core_ui.components.buttons.ThemeSwitcher
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.feat_menu.R
import com.trackhub.feat_menu.presentation.components.AccountHeader
import com.trackhub.feat_menu.presentation.components.AppVersionItem
import com.trackhub.feat_menu.presentation.components.SettingCard
import com.trackhub.feat_menu.presentation.mappers.toUI
import com.trackhub.feat_menu.presentation.models.ProfileUI
import org.koin.compose.koinInject

@Composable
fun MenuScreen(
    navigateToProfile: () -> Unit,
    menuViewModel: MenuViewModel = koinInject()
) {
    val baseState by menuViewModel.baseState.collectAsStateWithLifecycle()
    val menuState by menuViewModel.menuState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val versionName = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    BaseScreen(
        viewModel = menuViewModel,
        baseState = baseState
    ) {
        MenuContent(
            menuState = menuState,
            menuAction = { action ->
                when (action) {
                    is MenuAction.NavigateToProfile -> navigateToProfile()
                }
                menuViewModel.menuAction(action)
            },
            baseAction = menuViewModel::baseAction,
            versionName = versionName,
        )
    }
}

@Composable
private fun MenuContent(
    menuState: MenuState,
    menuAction: (MenuAction) -> Unit,
    baseAction: (BaseAction) -> Unit,
    versionName: String?
) {
    SetScaffold()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Account Header Section
        AccountHeader(
            profile = menuState.profile?.toUI() ?: ProfileUI(
                name = "User Name",
                email = "user@example.com",
                createdAt = "2022-01-01"
            )
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Settings Section
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.preferences),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Theme Setting Card
            SettingCard(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.theme),
                description = stringResource(
                    when (menuState.isDarkTheme) {
                        true -> R.string.dark_theme
                        false -> R.string.light_theme
                        else -> R.string.system_theme
                    }
                )
            ) {
                ThemeSwitcher(
                    darkTheme = menuState.isDarkTheme ?: isSystemInDarkTheme(),
                    onClick = { isDarkTheme -> menuAction(MenuAction.ChangeTheme(isDarkTheme)) },
                    size = 60.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Language Setting Card
            SettingCard(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language),
                description = stringResource(
                    if (menuState.isArabic) R.string.arabic else R.string.english
                )
            ) {
                LanguageSwitcher(
                    isArabic = menuState.isArabic,
                    onClick = { menuAction(MenuAction.ChangeLanguage(it)) },
                    size = 60.dp
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // App Information Section
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AppVersionItem(versionName = versionName ?: stringResource(R.string.unknown))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button at Bottom
        CustomButton(
            text = stringResource(R.string.logout),
            onClick = { menuAction(MenuAction.Logout) },
            variant = ButtonVariant.OUTLINED,
            leadingIconPainter = painterResource(R.drawable.exit_ic),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MenuContentPreview() {
    AppTheme {
        MenuContent(
            menuState = MenuState(),
            menuAction = {},
            baseAction = {},
            versionName = "1.0.0"
        )
    }
}