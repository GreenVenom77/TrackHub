package com.greenvenom.core_ui.components.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenvenom.core_ui.R
import com.greenvenom.core_ui.theme.AppTheme

enum class TextFieldVariant {
    OUTLINED,
    FILLED
}

enum class TextFieldState {
    DEFAULT,
    ERROR,
    SUCCESS,
    WARNING
}

enum class TextFieldSize {
    SMALL,
    MEDIUM,
    LARGE
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    successText: String? = null,
    warningText: String? = null,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    isRequired: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPasswordField: Boolean = false,
    variant: TextFieldVariant = TextFieldVariant.OUTLINED,
    state: TextFieldState = TextFieldState.DEFAULT,
    size: TextFieldSize = TextFieldSize.MEDIUM,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
    textStyle: TextStyle? = null,
    maxLines: Int = 1,
    minLines: Int = 1,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Determine the current state based on error, success, and warning texts
    val currentState = when {
        !errorText.isNullOrEmpty() -> TextFieldState.ERROR
        !successText.isNullOrEmpty() -> TextFieldState.SUCCESS
        !warningText.isNullOrEmpty() -> TextFieldState.WARNING
        else -> state
    }

    // Size configurations
    val (cornerRadius, iconSize) = when (size) {
        TextFieldSize.SMALL -> 8.dp to 18.dp
        TextFieldSize.MEDIUM -> 12.dp to 20.dp
        TextFieldSize.LARGE -> 16.dp to 24.dp
    }

    val defaultShape = shape ?: RoundedCornerShape(cornerRadius)
    val defaultTextStyle = textStyle ?: when (size) {
        TextFieldSize.SMALL -> MaterialTheme.typography.bodySmall
        TextFieldSize.MEDIUM -> MaterialTheme.typography.bodyMedium
        TextFieldSize.LARGE -> MaterialTheme.typography.bodyLarge
    }

    // Colors based on state and variant
    val defaultColors = colors ?: when (variant) {
        TextFieldVariant.OUTLINED -> OutlinedTextFieldDefaults.colors(
            focusedBorderColor = when (currentState) {
                TextFieldState.ERROR -> MaterialTheme.colorScheme.error
                TextFieldState.SUCCESS -> MaterialTheme.colorScheme.primary
                TextFieldState.WARNING -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            },
            unfocusedBorderColor = when (currentState) {
                TextFieldState.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                TextFieldState.SUCCESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                TextFieldState.WARNING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outline
            }
        )
        TextFieldVariant.FILLED -> TextFieldDefaults.colors(
            focusedIndicatorColor = when (currentState) {
                TextFieldState.ERROR -> MaterialTheme.colorScheme.error
                TextFieldState.SUCCESS -> MaterialTheme.colorScheme.primary
                TextFieldState.WARNING -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            },
            unfocusedIndicatorColor = when (currentState) {
                TextFieldState.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                TextFieldState.SUCCESS -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                TextFieldState.WARNING -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
            },
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.04f)
        )
    }

    // Status message and icon
    val (statusMessage, statusIcon, statusColor) = when {
        !errorText.isNullOrEmpty() -> Triple(
            errorText,
            R.drawable.error_ic,
            MaterialTheme.colorScheme.error
        )
        !successText.isNullOrEmpty() -> Triple(
            successText,
            R.drawable.check_circle_ic,
            MaterialTheme.colorScheme.primary
        )
        !warningText.isNullOrEmpty() -> Triple(
            warningText,
            R.drawable.info_ic,
            MaterialTheme.colorScheme.tertiary
        )
        !helperText.isNullOrEmpty() -> Triple(
            helperText,
            null,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        else -> Triple(null, null, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Column(modifier = modifier) {
        // Label with required indicator
        if (!label.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isRequired) {
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Text Field - Switch between Outlined and Filled variants
        when (variant) {
            TextFieldVariant.OUTLINED -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = defaultTextStyle,
                    placeholder = if (!placeholder.isNullOrEmpty()) {
                        { Text(placeholder) }
                    } else null,
                    leadingIcon = leadingIcon?.let { icon ->
                        {
                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    },
                    trailingIcon = {
                        TrailingIconContent(
                            isPasswordField = isPasswordField,
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                            trailingIcon = trailingIcon,
                            onTrailingIconClick = onTrailingIconClick,
                            maxLength = maxLength,
                            value = value,
                            maxLines = maxLines,
                            iconSize = iconSize
                        )
                    },
                    isError = currentState == TextFieldState.ERROR,
                    visualTransformation = if (isPasswordField && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                        capitalization = capitalization
                    ),
                    keyboardActions = keyboardActions,
                    singleLine = maxLines == 1,
                    maxLines = maxLines,
                    minLines = minLines,
                    shape = defaultShape,
                    colors = defaultColors
                )
            }
            TextFieldVariant.FILLED -> {
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (maxLength == null || newValue.length <= maxLength) {
                            onValueChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = defaultTextStyle,
                    placeholder = if (!placeholder.isNullOrEmpty()) {
                        { Text(placeholder) }
                    } else null,
                    leadingIcon = leadingIcon?.let { icon ->
                        {
                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    },
                    trailingIcon = {
                        TrailingIconContent(
                            isPasswordField = isPasswordField,
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                            trailingIcon = trailingIcon,
                            onTrailingIconClick = onTrailingIconClick,
                            maxLength = maxLength,
                            value = value,
                            maxLines = maxLines,
                            iconSize = iconSize
                        )
                    },
                    isError = currentState == TextFieldState.ERROR,
                    visualTransformation = if (isPasswordField && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction,
                        capitalization = capitalization
                    ),
                    keyboardActions = keyboardActions,
                    singleLine = maxLines == 1,
                    maxLines = maxLines,
                    minLines = minLines,
                    shape = defaultShape,
                    colors = defaultColors
                )
            }
        }

        // Status message
        AnimatedVisibility(
            visible = !statusMessage.isNullOrEmpty(),
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            ) {
                statusIcon?.let { iconId ->
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = statusMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun TrailingIconContent(
    isPasswordField: Boolean,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    trailingIcon: Painter?,
    onTrailingIconClick: (() -> Unit)?,
    maxLength: Int?,
    value: String,
    maxLines: Int,
    iconSize: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Character counter for large text fields
        if (maxLength != null && (maxLines > 1 || value.length > maxLength * 0.8)) {
            Text(
                text = "${value.length}/$maxLength",
                style = MaterialTheme.typography.labelSmall,
                color = if (value.length > maxLength * 0.9) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        // Password visibility toggle
        if (isPasswordField) {
            Icon(
                painter = if (passwordVisible) painterResource(R.drawable.visibility_off_ic)
                else painterResource(R.drawable.visibility_ic),
                contentDescription = if (passwordVisible) stringResource(R.string.hide_password)
                else stringResource(R.string.show_password),
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onPasswordVisibilityToggle() }
                    .padding(2.dp)
            )
        } else if (trailingIcon != null) {
            Icon(
                painter = trailingIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .then(
                        if (onTrailingIconClick != null) {
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onTrailingIconClick() }
                                .padding(2.dp)
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

// Convenience composables for common use cases
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.password),
    placeholder: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    variant: TextFieldVariant = TextFieldVariant.OUTLINED,
    imeAction: ImeAction = ImeAction.Done
) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        errorText = errorText,
        enabled = enabled,
        isRequired = required,
        isPasswordField = true,
        keyboardType = KeyboardType.Password,
        variant = variant,
        imeAction = imeAction
    )
}

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.email),
    placeholder: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    variant: TextFieldVariant = TextFieldVariant.OUTLINED,
    imeAction: ImeAction = ImeAction.Next
) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        errorText = errorText,
        enabled = enabled,
        isRequired = required,
        keyboardType = KeyboardType.Email,
        capitalization = KeyboardCapitalization.None,
        variant = variant,
        imeAction = imeAction
    )
}

@PreviewLightDark
@Composable
private fun CustomTextFieldPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // Outlined variants
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Email (Outlined)",
                placeholder = "Enter your email",
                helperText = "We'll never share your email",
                isRequired = true,
                keyboardType = KeyboardType.Email,
                variant = TextFieldVariant.OUTLINED
            )

            // Filled variants
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Email (Filled)",
                placeholder = "Enter your email",
                helperText = "We'll never share your email",
                isRequired = true,
                keyboardType = KeyboardType.Email,
                variant = TextFieldVariant.FILLED
            )

            PasswordField(
                value = "",
                onValueChange = {},
                label = "Password (Outlined)",
                errorText = "Password must be at least 8 characters",
                variant = TextFieldVariant.OUTLINED
            )

            PasswordField(
                value = "",
                onValueChange = {},
                label = "Password (Filled)",
                errorText = "Password must be at least 8 characters",
                variant = TextFieldVariant.FILLED
            )
        }
    }
}