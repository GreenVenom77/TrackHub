package com.greenvenom.core_ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenvenom.core_ui.R
import com.greenvenom.core_ui.theme.AppTheme

enum class ButtonVariant {
    PRIMARY,      // Filled button (default)
    SECONDARY,    // FilledTonal button
    OUTLINED,     // OutlinedButton
    TEXT,         // TextButton
    ELEVATED      // ElevatedButton
}

enum class ButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingIconPainter: Painter? = null,
    trailingIcon: ImageVector? = null,
    trailingIconPainter: Painter? = null,
    colors: ButtonColors? = null,
    shape: Shape? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues? = null,
    fillMaxWidth: Boolean = true,
    textStyle: TextStyle? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    // Animation for press effect
    val scale by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 1f else 0.95f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "button_scale"
    )

    // Size configurations
    val (buttonHeight, horizontalPadding, verticalPadding, iconSize, textSize) = when (size) {
        ButtonSize.SMALL -> Tuple5(40.dp, 16.dp, 8.dp, 16.dp, 14.sp)
        ButtonSize.MEDIUM -> Tuple5(48.dp, 20.dp, 12.dp, 20.dp, 16.sp)
        ButtonSize.LARGE -> Tuple5(56.dp, 24.dp, 16.dp, 24.dp, 18.sp)
    }

    val defaultContentPadding = contentPadding ?: PaddingValues(
        horizontal = horizontalPadding,
        vertical = verticalPadding
    )

    val defaultShape = shape ?: RoundedCornerShape(
        when (size) {
            ButtonSize.SMALL -> 8.dp
            ButtonSize.MEDIUM -> 12.dp
            ButtonSize.LARGE -> 16.dp
        }
    )

    val defaultTextStyle = textStyle ?: MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = textSize
    )

    val buttonModifier = modifier
        .let { if (fillMaxWidth) it.fillMaxWidth() else it }
        .scale(scale)
        .height(buttonHeight)

    val buttonContent: @Composable (RowScope.() -> Unit) = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(defaultContentPadding)
        ) {
            // Leading icon or loading indicator
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(iconSize),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                leadingIcon != null -> {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                leadingIconPainter != null -> {
                    Icon(
                        painter = leadingIconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // Button text
            Text(
                text = if (isLoading) stringResource(R.string.loading) else text,
                textAlign = TextAlign.Center,
                style = defaultTextStyle,
                modifier = Modifier.weight(1f)
            )

            // Trailing icon
            when {
                trailingIcon != null -> {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
                trailingIconPainter != null -> {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = trailingIconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }

    when (variant) {
        ButtonVariant.PRIMARY -> {
            Button(
                onClick = { if (!isLoading) onClick() },
                modifier = buttonModifier,
                enabled = enabled && !isLoading,
                shape = defaultShape,
                colors = colors ?: ButtonDefaults.buttonColors(),
                border = border,
                contentPadding = PaddingValues(0.dp),
                interactionSource = interactionSource,
                content = buttonContent
            )
        }
        ButtonVariant.SECONDARY -> {
            FilledTonalButton(
                onClick = { if (!isLoading) onClick() },
                modifier = buttonModifier,
                enabled = enabled && !isLoading,
                shape = defaultShape,
                colors = colors ?: ButtonDefaults.filledTonalButtonColors(),
                border = border,
                contentPadding = PaddingValues(0.dp),
                interactionSource = interactionSource,
                content = buttonContent
            )
        }
        ButtonVariant.OUTLINED -> {
            OutlinedButton(
                onClick = { if (!isLoading) onClick() },
                modifier = buttonModifier,
                enabled = enabled && !isLoading,
                shape = defaultShape,
                colors = colors ?: ButtonDefaults.outlinedButtonColors(),
                border = border ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = PaddingValues(0.dp),
                interactionSource = interactionSource,
                content = buttonContent
            )
        }
        ButtonVariant.TEXT -> {
            TextButton(
                onClick = { if (!isLoading) onClick() },
                modifier = buttonModifier,
                enabled = enabled && !isLoading,
                shape = defaultShape,
                colors = colors ?: ButtonDefaults.textButtonColors(),
                border = border,
                contentPadding = PaddingValues(0.dp),
                interactionSource = interactionSource,
                content = buttonContent
            )
        }
        ButtonVariant.ELEVATED -> {
            ElevatedButton(
                onClick = { if (!isLoading) onClick() },
                modifier = buttonModifier,
                enabled = enabled && !isLoading,
                shape = defaultShape,
                colors = colors ?: ButtonDefaults.elevatedButtonColors(),
                border = border,
                contentPadding = PaddingValues(0.dp),
                interactionSource = interactionSource,
                content = buttonContent
            )
        }
    }
}

// Helper data class for multiple return values
private data class Tuple5<T1, T2, T3, T4, T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5
)

// Convenience composables for common use cases
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    CustomButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = ButtonVariant.PRIMARY,
        enabled = enabled,
        isLoading = isLoading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        size = size
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    CustomButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = ButtonVariant.SECONDARY,
        enabled = enabled,
        isLoading = isLoading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        size = size
    )
}

@PreviewLightDark
@Composable
private fun CustomButtonPreview() {
    var isLoading by remember { mutableStateOf(false) }
    AppTheme {
        Column {
            CustomButton(
                text = "Primary Button",
                onClick = { isLoading = !isLoading },
                variant = ButtonVariant.SECONDARY,
                size = ButtonSize.MEDIUM,
                isLoading = isLoading
            )
        }
    }
}