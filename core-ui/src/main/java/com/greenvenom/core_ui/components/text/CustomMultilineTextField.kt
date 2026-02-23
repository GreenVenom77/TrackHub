package com.greenvenom.core_ui.components.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.R
import com.greenvenom.core_ui.theme.AppTheme

enum class MultilineTextFieldVariant {
    OUTLINED,
    FILLED
}

@Composable
fun CustomMultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    variant: MultilineTextFieldVariant = MultilineTextFieldVariant.FILLED,
    shape: Shape = RoundedCornerShape(12.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLength: Int? = null,
    minHeight: Dp = 100.dp,
    maxHeight: Dp = 200.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    showCharacterCount: Boolean = true,
    isRequired: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Determine colors based on state and focus
    val isError = !errorText.isNullOrEmpty()

    val backgroundColor = when (variant) {
        MultilineTextFieldVariant.FILLED -> {
            if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceContainer
        }
        MultilineTextFieldVariant.OUTLINED -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFocused -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    val borderWidth = if (variant == MultilineTextFieldVariant.OUTLINED) {
        if (isFocused) 2.dp else 1.dp
    } else 0.dp

    Column(modifier = modifier) {
        // Label with required indicator
        if (!label.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Medium
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

        // Text Field Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight, max = maxHeight)
                .clip(shape)
                .background(backgroundColor)
                .then(
                    if (borderWidth > 0.dp) {
                        Modifier.border(
                            width = borderWidth,
                            color = borderColor,
                            shape = shape
                        )
                    } else Modifier
                )
                .padding(16.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (maxLength == null || newValue.length <= maxLength) {
                        onValueChange(newValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction,
                    capitalization = capitalization
                ),
                keyboardActions = keyboardActions,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (value.isEmpty() && !placeholder.isNullOrEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // Character count and helper/error text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Helper/Error text
            AnimatedVisibility(
                visible = !helperText.isNullOrEmpty() || !errorText.isNullOrEmpty(),
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(),
                modifier = Modifier.weight(1f)
            ) {
                val message = errorText ?: helperText ?: ""
                val color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = color
                )
            }

            // Character count
            if (showCharacterCount && maxLength != null) {
                val currentLength = value.length
                val isNearLimit = currentLength > maxLength * 0.8
                val isOverLimit = currentLength > maxLength * 0.95

                Text(
                    text = "$currentLength/$maxLength",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isOverLimit -> MaterialTheme.colorScheme.error
                        isNearLimit -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

// Convenience composables for common use cases
@Composable
fun CommentField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.comment),
    placeholder: String = stringResource(R.string.write_your_comment_here),
    maxLength: Int = 500,
    required: Boolean = false
) {
    CustomMultilineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        maxLength = maxLength,
        isRequired = required,
        variant = MultilineTextFieldVariant.FILLED,
        minHeight = 80.dp,
        maxHeight = 150.dp
    )
}

@Composable
fun DescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.description),
    placeholder: String = stringResource(R.string.enter_description),
    maxLength: Int = 1000,
    required: Boolean = false
) {
    CustomMultilineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        maxLength = maxLength,
        isRequired = required,
        variant = MultilineTextFieldVariant.OUTLINED,
        minHeight = 120.dp,
        maxHeight = 200.dp
    )
}

@Composable
fun NoteField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.add_a_note),
    maxLength: Int = 300
) {
    CustomMultilineTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        maxLength = maxLength,
        variant = MultilineTextFieldVariant.FILLED,
        minHeight = 60.dp,
        maxHeight = 120.dp,
        shape = RoundedCornerShape(8.dp)
    )
}

@PreviewLightDark
@Composable
private fun CustomMultilineTextFieldPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            CustomMultilineTextField(
                value = "",
                onValueChange = {},
                label = "Description",
                placeholder = "Enter your description here...",
                helperText = "Provide a detailed description",
                maxLength = 500,
                isRequired = true,
                variant = MultilineTextFieldVariant.OUTLINED
            )

            CommentField(
                value = "This is a sample comment that shows how the text field handles longer content.",
                onValueChange = {}
            )

            NoteField(
                value = "",
                onValueChange = {}
            )
        }
    }
}