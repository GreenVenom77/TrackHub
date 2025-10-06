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
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_ui.components.TopAppBar
import com.greenvenom.core_ui.presentation.ScaffoldViewModel
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_ui.utils.LocalScaffoldViewModel
import com.seravian.core_local.domain.AppPrefsDataSource
import com.skewnexus.trackhub.navigation.AppNavHost
import com.trackhub.feat_navigation.components.BottomNavigationBar
import com.trackhub.feat_navigation.routes.Screen
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val appPrefsRepository = koinInject<AppPrefsDataSource>()
            val appPrefState by appPrefsRepository.appPrefsState.collectAsStateWithLifecycle()
            val navigationRepository = koinInject<NavigationStateRepository>()
            val navigationState by navigationRepository.navigationState.collectAsStateWithLifecycle()
            val scaffoldViewModel: ScaffoldViewModel = koinInject()
            val scaffoldState by scaffoldViewModel.scaffoldState.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalScaffoldViewModel provides scaffoldViewModel) {
                AppTheme(darkTheme = appPrefState.isDarkTheme ?: isSystemInDarkTheme()) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                isVisible = navigationState.topBarState,
                                title = scaffoldState.title,
                                showLogo = scaffoldState.showLogo,
                                navigateBack = scaffoldState.navigateBackAction,
                                action = scaffoldState.topBarActions
                            )
                        },
                        floatingActionButton = scaffoldState.floatingActionButton,
                        bottomBar = {
                            BottomNavigationBar(
                                defaultNavigationMethod = navigationRepository::navigate,
                                currentDestination = navigationState.currentDestination ?: Screen.MyHubs,
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