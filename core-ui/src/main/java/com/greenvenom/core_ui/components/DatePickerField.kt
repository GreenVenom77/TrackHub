package com.greenvenom.core_ui.components

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.R
import com.greenvenom.core_ui.theme.AppTheme
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DateFormat {
    DD_MM_YYYY,    // 01/12/2023
    MM_DD_YYYY,    // 12/01/2023
    YYYY_MM_DD,    // 2023-12-01
    DD_MMM_YYYY,   // 01 Dec 2023
    MMM_DD_YYYY,   // Dec 01, 2023
    FULL           // December 01, 2023
}

@Composable
fun DatePickerField(
    selectedDate: Long? = null,
    modifier: Modifier = Modifier,
    onDateSelected: (Long?) -> Unit,
    label: String? = null,
    placeholder: String = stringResource(R.string.select_date),
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    isRequired: Boolean = false,
    allowClear: Boolean = true,
    dateFormat: DateFormat = DateFormat.DD_MM_YYYY,
    minDate: Long? = null,
    maxDate: Long? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: TextFieldColors? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    leadingIcon: Painter = painterResource(R.drawable.calendar_today_ic)
) {
    var showModal by rememberSaveable { mutableStateOf(false) }

    val isError = !errorText.isNullOrEmpty()

    val defaultColors = colors ?: OutlinedTextFieldDefaults.colors(
        focusedBorderColor = if (isError) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        unfocusedBorderColor = if (isError) {
            MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.outline
        }
    )

    val formattedDate by remember(selectedDate, dateFormat) {
        derivedStateOf {
            selectedDate?.let { formatDate(it, dateFormat) } ?: ""
        }
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

        // Date Field
        OutlinedTextField(
            value = formattedDate,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(selectedDate) {
                    if (enabled) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                showModal = true
                            }
                        }
                    }
                },
            enabled = enabled,
            readOnly = true,
            textStyle = textStyle,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    painter = leadingIcon,
                    contentDescription = stringResource(R.string.calendar),
                    tint = if (enabled) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = if (allowClear && selectedDate != null && enabled) {
                {
                    IconButton(
                        onClick = { onDateSelected(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear_date),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            isError = isError,
            shape = shape,
            colors = defaultColors
        )

        // Helper/Error text
        AnimatedVisibility(
            visible = !helperText.isNullOrEmpty() || !errorText.isNullOrEmpty(),
            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            ) {
                if (isError) {
                    Icon(
                        painter = painterResource(R.drawable.error_ic),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = errorText ?: helperText ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }

    if (showModal) {
        DatePickerModal(
            initialDate = selectedDate,
            minDate = minDate,
            maxDate = maxDate,
            onDateSelected = { date ->
                onDateSelected(date)
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialDate: Long? = null,
    minDate: Long? = null,
    maxDate: Long? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate,
        yearRange = getYearRange(minDate, maxDate)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

private fun getYearRange(minDate: Long?, maxDate: Long?): IntRange {
    val calendar = Calendar.getInstance()

    val minYear = minDate?.let {
        calendar.timeInMillis = it
        calendar.get(Calendar.YEAR)
    } ?: 1900

    val maxYear = maxDate?.let {
        calendar.timeInMillis = it
        calendar.get(Calendar.YEAR)
    } ?: 2100

    return minYear..maxYear
}

private fun formatDate(millis: Long, format: DateFormat): String {
    val formatter = when (format) {
        DateFormat.DD_MM_YYYY -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        DateFormat.MM_DD_YYYY -> SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        DateFormat.YYYY_MM_DD -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        DateFormat.DD_MMM_YYYY -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        DateFormat.MMM_DD_YYYY -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        DateFormat.FULL -> SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    }
    return formatter.format(Date(millis))
}

// Convenience composables for common use cases
@Composable
fun BirthDateField(
    selectedDate: Long? = null,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.date_of_birth),
    required: Boolean = false
) {
    val maxDate = System.currentTimeMillis() // Can't select future dates
    val minDate = Calendar.getInstance().apply {
        add(Calendar.YEAR, -150) // 150 years ago as reasonable limit
    }.timeInMillis

    DatePickerField(
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        modifier = modifier,
        label = label,
        placeholder = stringResource(R.string.select_your_birth_date),
        isRequired = required,
        minDate = minDate,
        maxDate = maxDate,
        dateFormat = DateFormat.DD_MMM_YYYY
    )
}

@Composable
fun EventDateField(
    selectedDate: Long? = null,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.event_date),
    required: Boolean = false
) {
    val minDate = System.currentTimeMillis() // Can't select past dates

    DatePickerField(
        selectedDate = selectedDate,
        onDateSelected = onDateSelected,
        modifier = modifier,
        label = label,
        placeholder = stringResource(R.string.select_event_date),
        isRequired = required,
        minDate = minDate,
        dateFormat = DateFormat.FULL
    )
}

@PreviewLightDark
@Composable
private fun DatePickerFieldPreview() {
    AppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            DatePickerField(
                label = "Select Date",
                placeholder = "Choose a date",
                helperText = "Pick any date you prefer",
                isRequired = true,
                onDateSelected = {}
            )

            BirthDateField(
                onDateSelected = {},
                required = true
            )

            EventDateField(
                selectedDate = System.currentTimeMillis(),
                onDateSelected = {}
            )
        }
    }
}