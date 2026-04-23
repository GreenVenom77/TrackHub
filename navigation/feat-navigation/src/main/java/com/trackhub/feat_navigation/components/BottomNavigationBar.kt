package com.trackhub.feat_navigation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.greenvenom.core_navigation.routes.Destination
import com.greenvenom.core_navigation.utils.NavigationType
import com.trackhub.feat_navigation.R
import com.trackhub.feat_navigation.routes.Screen

@Composable
fun BottomNavigationBar(
    defaultNavigationMethod: (NavigationType) -> Unit,
    currentDestination: Destination,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutLinearInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        ),
        content = {
            BottomBarContent(
                defaultNavigationMethod = defaultNavigationMethod,
                currentDestination = currentDestination
            )
        },
        modifier = modifier
    )
}

@Composable
private fun BottomBarContent(
    defaultNavigationMethod: (NavigationType) -> Unit,
    currentDestination: Destination
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        BottomDestination.entries.forEach { destination ->
            NavigationBarItem(
                onClick = {
                    if (destination.comparableScreen != currentDestination) {
                        defaultNavigationMethod(NavigationType.BottomNavigation(
                            destination = destination.comparableScreen
                        ))
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = stringResource(
                            R.string.navigation_icon,
                            stringResource(destination.label)
                        )
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.label),
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = destination.comparableScreen == currentDestination,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomNavigationBarContent() {
    BottomBarContent(
        defaultNavigationMethod = {  },
        currentDestination = Screen.SharedHubs()
    )
}
