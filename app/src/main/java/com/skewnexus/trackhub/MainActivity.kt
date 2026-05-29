package com.skewnexus.trackhub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.greenvenom.core_ui.components.bars.TopAppBar
import com.greenvenom.core_ui.presentation.ScaffoldViewModel
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_ui.utils.LocalScaffoldViewModel
import com.greenvenom.core_util.theme.ThemeManager
import com.skewnexus.trackhub.navigation.AppNavHost
import com.trackhub.feat_navigation.components.BottomNavigationBar
import com.trackhub.feat_navigation.routes.Screen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val themeManager = koinInject<ThemeManager>()
            val isDarkTheme by themeManager.isDarkThemeFlow.collectAsStateWithLifecycle(isSystemInDarkTheme())
            val navigationRepository = koinInject<NavigationRepository>()
            val navigationState by navigationRepository.navigationData.collectAsStateWithLifecycle()
            val scaffoldViewModel: ScaffoldViewModel = koinViewModel()
            val scaffoldState by scaffoldViewModel.scaffoldState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalScaffoldViewModel provides scaffoldViewModel) {
                AppTheme(darkTheme = isDarkTheme) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                isVisible = navigationState.topBarState,
                                title = scaffoldState.title,
                                showLogo = scaffoldState.showLogo,
                                showSearchBar = scaffoldState.showSearchBar,
                                onSearchQueryChange = scaffoldState.onSearchQueryChange,
                                navigateBack = scaffoldState.navigateBackAction,
                                action = scaffoldState.topBarActions
                            )
                        },
                        floatingActionButton = scaffoldState.floatingActionButton,
                        bottomBar = {
                            BottomNavigationBar(
                                defaultNavigationMethod = navigationRepository::navigate,
                                currentDestination = navigationState.currentDestination ?: Screen.OwnedHubs(),
                                isVisible = navigationState.bottomBarState
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    ) { innerPadding ->
                        AppNavHost(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}