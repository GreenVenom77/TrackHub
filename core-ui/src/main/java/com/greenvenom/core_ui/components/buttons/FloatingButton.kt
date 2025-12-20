package com.greenvenom.core_ui.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.R
import com.greenvenom.core_ui.theme.AppTheme

enum class FloatingButtonSize {
    MEDIUM,     // 56dp (default)
    LARGE       // 96dp
}

enum class FloatingButtonVariant {
    REGULAR,        // Standard FAB
    EXTENDED,       // Extended FAB with text
    MINI            // Small FAB
}

@Composable
fun FloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    icon: Painter = painterResource(R.drawable.add_ic),
    contentDescription: String? = null,
    text: String? = null,
    variant: FloatingButtonVariant = FloatingButtonVariant.REGULAR,
    size: FloatingButtonSize = FloatingButtonSize.MEDIUM,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    shape: Shape? = null,
    elevation: Dp = 6.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    animateIcon: Boolean = true,
    rotateOnClick: Boolean = false
) {
    // Icon rotation animation
    val iconRotation by animateFloatAsState(
        targetValue = if (rotateOnClick && animateIcon) 45f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "icon_rotation"
    )

    val defaultShape = shape ?: when (variant) {
        FloatingButtonVariant.EXTENDED -> RoundedCornerShape(16.dp)
        else -> CircleShape
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(300)
        ) + scaleIn(
            animationSpec = tween(300),
            initialScale = 0.8f
        ) + slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it / 2 }
        ),
        exit = fadeOut(
            animationSpec = tween(300)
        ) + scaleOut(
            animationSpec = tween(300),
            targetScale = 0.8f
        ) + slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it / 2 }
        )
    ) {
        when (variant) {
            FloatingButtonVariant.EXTENDED -> {
                ExtendedFloatingActionButton(
                    onClick = onClick,
                    modifier = modifier,
                    shape = defaultShape,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
                    interactionSource = interactionSource
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = contentDescription,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(iconRotation)
                        )
                        if (!text.isNullOrEmpty()) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            FloatingButtonVariant.MINI -> {
                SmallFloatingActionButton(
                    onClick = onClick,
                    modifier = modifier,
                    shape = defaultShape,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
                    interactionSource = interactionSource
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(iconRotation)
                    )
                }
            }

            FloatingButtonVariant.REGULAR -> {
                when (size) {
                    FloatingButtonSize.LARGE -> {
                        LargeFloatingActionButton(
                            onClick = onClick,
                            modifier = modifier,
                            shape = defaultShape,
                            containerColor = containerColor,
                            contentColor = contentColor,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
                            interactionSource = interactionSource
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = contentDescription,
                                modifier = Modifier
                                    .size(16.dp)
                                    .rotate(iconRotation)
                            )
                        }
                    }

                    FloatingButtonSize.MEDIUM -> {
                        FloatingActionButton(
                            onClick = onClick,
                            modifier = modifier,
                            shape = defaultShape,
                            containerColor = containerColor,
                            contentColor = contentColor,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation),
                            interactionSource = interactionSource
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = contentDescription,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(iconRotation)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Convenience composables for common use cases
@Composable
fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    text: String? = null,
    variant: FloatingButtonVariant = if (text != null) FloatingButtonVariant.EXTENDED else FloatingButtonVariant.REGULAR
) {
    FloatingButton(
        onClick = onClick,
        modifier = modifier,
        isVisible = isVisible,
        icon = painterResource(R.drawable.add_ic),
        contentDescription = stringResource(R.string.add),
        text = text,
        variant = variant,
        animateIcon = true,
        rotateOnClick = true
    )
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onTertiaryContainer
) {
    FloatingButton(
        onClick = onClick,
        modifier = modifier,
        isVisible = isVisible,
        icon = icon,
        contentDescription = contentDescription,
        containerColor = containerColor,
        contentColor = contentColor,
        variant = FloatingButtonVariant.REGULAR
    )
}

@Composable
fun MiniFab(
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    FloatingButton(
        onClick = onClick,
        modifier = modifier,
        isVisible = isVisible,
        icon = icon,
        contentDescription = contentDescription,
        variant = FloatingButtonVariant.MINI,
        containerColor = containerColor,
        contentColor = contentColor
    )
}

// Multi-Action FAB that expands to show multiple options
@Composable
fun MultiActionFab(
    mainIcon: Painter,
    actions: List<FabAction>,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit,
    isVisible: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Action buttons
            actions.forEachIndexed { index, action ->
                AnimatedVisibility(
                    visible = isExpanded && isVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 150,
                            delayMillis = index * 50
                        )
                    ) + slideInVertically(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = index * 50
                        ),
                        initialOffsetY = { it / 2 }
                    ),
                    exit = fadeOut(
                        animationSpec = tween(100)
                    ) + slideOutVertically(
                        animationSpec = tween(100),
                        targetOffsetY = { it / 2 }
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Action label
                        if (!action.label.isNullOrEmpty()) {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    )
                            )
                        }

                        // Action button
                        MiniFab(
                            onClick = {
                                action.onClick()
                                onExpandedChange(false)
                            },
                            icon = action.icon,
                            contentDescription = action.contentDescription,
                            containerColor = action.containerColor ?: MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = action.contentColor ?: MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Main FAB
            FloatingButton(
                onClick = { onExpandedChange(!isExpanded) },
                isVisible = isVisible,
                icon = mainIcon,
                contentDescription = if (isExpanded) stringResource(R.string.close_menu)
                else stringResource(R.string.open_menu),
                containerColor = containerColor,
                contentColor = contentColor,
                animateIcon = true,
                rotateOnClick = isExpanded
            )
        }
    }
}

// Data class for multi-action FAB actions
data class FabAction(
    val icon: Painter,
    val onClick: () -> Unit,
    val label: String? = null,
    val contentDescription: String? = null,
    val containerColor: Color? = null,
    val contentColor: Color? = null
)

@PreviewLightDark
@Composable
private fun FloatingButtonPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {
            // Regular FAB
            FloatingButton(
                onClick = {},
                isVisible = true
            )

            // Extended FAB
            AddButton(
                onClick = {},
                text = "Add Item"
            )

            // Mini FAB
            MiniFab(
                onClick = {},
                icon = painterResource(R.drawable.add_ic)
            )

            // Large FAB
            FloatingButton(
                onClick = {},
                size = FloatingButtonSize.LARGE,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}