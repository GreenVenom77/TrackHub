package com.skewnexus.trackhub

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.greenvenom.core_navigation.data.NavigationType
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_navigation.data.repository.NavigationStateRepository
import com.greenvenom.core_ui.components.FloatingButton
import com.greenvenom.core_menu.data.AppPrefStateRepository
import com.skewnexus.trackhub.navigation.AppNavHost
import com.trackhub.feat_navigation.components.BottomNavigationBar
import com.trackhub.feat_navigation.components.TopAppBar
import com.trackhub.feat_navigation.data.DestinationHandler
import com.trackhub.feat_navigation.routes.Screen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.compose.koinInject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context: Context = this
        val appPrefStateRepository = getKoin().get<AppPrefStateRepository>()

        lifecycleScope.launch {
            appPrefStateRepository.getThemePreference(context).collect {
                appPrefStateRepository.changeTheme(context, it)
                WindowCompat.getInsetsController(window, window.decorView)
                    .isAppearanceLightStatusBars = !it
            }
        }

        setContent {
            val navigationRepository = koinInject<NavigationStateRepository>()
            val navigationState by navigationRepository.navigationState.collectAsStateWithLifecycle()

            val destinationHandler = koinInject<DestinationHandler>()
            val destinationState by destinationHandler.destinationState.collectAsStateWithLifecycle()

            val appPrefState by appPrefStateRepository.appPrefState.collectAsStateWithLifecycle()

            AppTheme(darkTheme = appPrefState.isDarkTheme) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = destinationState.currentHub?.name ?: stringResource(R.string.app_name),
                            isVisible = navigationState.topBarState,
                            isSideDestination = navigationState.isCurrentDestinationSide,
                            navigateBack = {
                                navigationRepository.navigate(NavigationType.Back)
                                destinationHandler.destinationState.update {
                                    it.copy(
                                        currentHub = null
                                    )
                                }
                            },
                            isActionEnabled = navigationState.currentDestination is Screen.HubDetails,
                            action = {
                                if (navigationState.currentDestination is Screen.HubDetails) {
                                    IconButton(
                                        onClick = {
                                            destinationHandler.destinationState.update {
                                                it.copy(
                                                    hubBottomSheetState = true
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(8.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.edit_ic),
                                            contentDescription = stringResource(R.string.edit_hub_Details),
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            defaultNavigationMethod = navigationRepository::navigate,
                            currentDestination = navigationState.currentDestination ?: Screen.MyHubs,
                            isVisible = navigationState.bottomBarState
                        )
                    },
                    floatingActionButton = {
                        FloatingButton(
                            isVisible = navigationState.currentDestination is Screen.HubDetails
                                || navigationState.currentDestination is Screen.MyHubs,
                            onClick = {
                                destinationHandler.destinationState.update {
                                    if (navigationState.currentDestination is Screen.HubDetails) {
                                        it.copy(
                                            itemBottomSheetState = true
                                        )
                                    } else {
                                        it.copy(
                                            hubBottomSheetState = true
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                        )
                    },
                    floatingActionButtonPosition = FabPosition.End
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