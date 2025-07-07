package com.skewnexus.trackhub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.seravian.core_local.domain.AppPrefsDataSource
import com.skewnexus.trackhub.navigation.AppNavHost
import com.trackhub.feat_navigation.components.BottomNavigationBar
import com.trackhub.feat_navigation.routes.Screen
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appPrefsRepository = koinInject<AppPrefsDataSource>()
            val appPrefState by appPrefsRepository.appPrefsState.collectAsStateWithLifecycle()
            val navigationRepository = koinInject<NavigationStateRepository>()
            val navigationState by navigationRepository.navigationState.collectAsStateWithLifecycle()

            AppTheme(darkTheme = appPrefState.isDarkTheme ?: isSystemInDarkTheme()) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppNavHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )

                        BottomNavigationBar(
                            defaultNavigationMethod = navigationRepository::navigate,
                            currentDestination = navigationState.currentDestination ?: Screen.MyHubs,
                            isVisible = navigationState.bottomBarState
                        )
                    }
                }
            }
        }
    }
}