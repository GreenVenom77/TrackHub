package com.skewnexus.trackhub

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_menu.data.AppPrefStateRepository
import com.skewnexus.trackhub.navigation.AppNavHost
import com.trackhub.feat_navigation.components.BottomNavigationBar
import com.trackhub.feat_navigation.routes.Screen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val appPrefRepository = koinInject<AppPrefStateRepository>()
            val appPrefState by appPrefRepository.appPrefState.collectAsStateWithLifecycle()
            val navigationRepository = koinInject<NavigationStateRepository>()
            val navigationState by navigationRepository.navigationState.collectAsStateWithLifecycle()

            DisposableEffect (Unit) {
                val themeJob = coroutineScope.launch {
                    appPrefRepository.getThemePreference(context).collect {
                        appPrefRepository.changeTheme(context, it)
                        WindowCompat.getInsetsController(window, window.decorView)
                            .isAppearanceLightStatusBars = !it
                    }
                }
                onDispose { themeJob.cancel() }
            }

            AppTheme(darkTheme = appPrefState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
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