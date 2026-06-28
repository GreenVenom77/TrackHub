@file:OptIn(ExperimentalMaterial3Api::class)

package com.greenvenom.core_ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.R

@Composable
fun <T> DialogSearchableDropdown(
    modifier: Modifier = Modifier,
    selectedOption: T? = null,
    selectedOptions: List<T> = emptyList(),
    onItemSelected: (item: T) -> Unit = {},
    onItemsSelected: (items: List<T>) -> Unit = {},
    multipleSelection: Boolean = false,
    selectedItemToString: (T) -> String = { it.toString() },
    selectedItemsToString: ((List<T>) -> String)? = null,
    title: String? = null,
    placeholder: String,
    options: List<T>,
    enabled: Boolean = true,
    isError: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.background,
        errorContainerColor = MaterialTheme.colorScheme.background,
    ),
    fieldLabelTextStyle: TextStyle = MaterialTheme.typography.labelSmall,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    placeholderTextStyle: TextStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
    ),
    drawItem: @Composable (T, Boolean, Boolean, () -> Unit) -> Unit = { item, selected, itemEnabled, onClick ->
        DialogDropdownMenuItem(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            text = selectedItemToString(item),
            selected = selected,
            enabled = itemEnabled,
            onClick = onClick,
            textStyle = textStyle,
            multipleSelection = multipleSelection,
        )
    },
    optionsTitle: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val showSearchOption by remember {
        derivedStateOf {
            options.size > 5
        }
    }

    val resources = LocalResources.current
    val defaultSelectedItemsToString: (List<T>) -> String = { items ->
        when {
            items.isEmpty() -> ""
            items.size == 1 -> items.first().toString()
            else -> resources.getString(R.string.selected_items, items.size)
        }
    }

    val displayText = if (multipleSelection) {
        if (selectedOptions.isNotEmpty()) {
            (selectedItemsToString ?: defaultSelectedItemsToString)(selectedOptions)
        } else ""
    } else {
        if (selectedOption != null) selectedItemToString(selectedOption) else ""
    }

    Column {
        if (title != null) {
            Text(
                text = title,
                style = fieldLabelTextStyle,
            )
        }
        Box(modifier = modifier.height(IntrinsicSize.Min)) {
            OutlinedTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                readOnly = true,
                value = displayText,
                onValueChange = {},
                shape = shape,
                textStyle = textStyle,
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = placeholderTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                trailingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        trailingIcon?.invoke()
                    }
                },
                colors = colors,
                isError = isError,
            )

            // Transparent clickable surface on top of OutlinedTextField
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(enabled = enabled) { expanded = true },
                color = Color.Transparent,
            ) {}
        }
    }

    if (expanded) {
        var searchedString by rememberSaveable { mutableStateOf("") }
        var filteredItems by remember { mutableStateOf(options) }

        // Track selected items for multiple selection mode
        var currentSelectedItems by remember { mutableStateOf(selectedOptions) }

        val items = if (searchedString.isEmpty()) {
            options
        } else {
            filteredItems
        }

        PromptDialog(
            modifier = Modifier
                .wrapContentHeight()
                .heightIn(max = 400.dp)
                .fillMaxWidth(),
            onDismiss = {
                expanded = false
                // Reset search
                searchedString = ""
                // Reset selection state for multiple selection
                if (multipleSelection) {
                    currentSelectedItems = selectedOptions
                }
            },
            title = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    optionsTitle?.invoke()
                    if (showSearchOption) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = searchedString,
                            onValueChange = { searchStr ->
                                searchedString = searchStr
                                filteredItems = options.searchForItem(searchStr)
                            },
                            textStyle = textStyle,
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.search),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                        )
                    }
                }
            },
            confirmButtonContent = if (multipleSelection) {
                {
                    TextButton(
                        onClick = {
                            onItemsSelected(currentSelectedItems)
                            expanded = false
                            searchedString = ""
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            } else {
                {}
            },
            dismissButtonContent = if (multipleSelection) {
                {
                    TextButton(
                        onClick = {
                            expanded = false
                            searchedString = ""
                            currentSelectedItems = selectedOptions
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            } else {
                {}
            }
        ) {
            val listState = rememberLazyListState()

            // Scroll to selected item for single selection
            if (!multipleSelection && selectedOption != null) {
                val index = options.indexOf(selectedOption)
                if (index != -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = index)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
            ) {
                itemsIndexed(items) { index, item ->
                    val selectedItem = if (multipleSelection) {
                        currentSelectedItems.contains(item)
                    } else {
                        item == selectedOption
                    }

                    drawItem(
                        item,
                        selectedItem,
                        true
                    ) {
                        if (multipleSelection) {
                            // Toggle selection for multiple selection
                            currentSelectedItems = if (currentSelectedItems.contains(item)) {
                                currentSelectedItems - item
                            } else {
                                currentSelectedItems + item
                            }
                        } else {
                            // Single selection - close dialog immediately
                            onItemSelected(item)
                            expanded = false
                            searchedString = ""
                        }
                    }

                    if (index < items.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DialogDropdownMenuItem(
    text: String,
    textStyle: TextStyle,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    multipleSelection: Boolean = false,
) {
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Row(
            modifier = modifier
                .clickable(enabled) { onClick() }
                .fillMaxWidth()
                .padding(horizontal = if (multipleSelection) 8.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (multipleSelection) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = null, // Handled by parent click
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
            Text(
                text = text,
                style = textStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PromptDialog(
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit = {},
    dismissButtonContent: @Composable () -> Unit = {},
    confirmButtonContent: @Composable () -> Unit = {},
    dismissable: Boolean = true,
    dialogBackgroundColor: Color = MaterialTheme.colorScheme.background,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        icon = icon,
        containerColor = dialogBackgroundColor,
        onDismissRequest = {
            if (dismissable) onDismiss()
        },
        title = title,
        text = content,
        dismissButton = dismissButtonContent,
        confirmButton = confirmButtonContent,
    )
}

// Preview for single selection
@Preview
@Composable
private fun DialogSearchableDropdownSinglePreview() {
    DialogSearchableDropdown(
        options = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"),
        selectedOption = "Item 2",
        onItemSelected = {},
        multipleSelection = false,
        optionsTitle = { Text(text = stringResource(R.string.select_an_item)) },
        placeholder = stringResource(R.string.select_an_item),
    )
}

// Preview for multiple selection
@Preview
@Composable
private fun DialogSearchableDropdownMultiplePreview() {
    DialogSearchableDropdown(
        options = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6"),
        selectedOptions = listOf("Item 2", "Item 4"),
        onItemsSelected = {},
        multipleSelection = true,
        optionsTitle = { Text(text = stringResource(R.string.select_items)) },
        placeholder = stringResource(R.string.select_items),
    )
}

internal fun <T> List<T>.searchForItem(
    searchStr: String,
): List<T> {
    val filteredItems = filter {
        it.toString().contains(
            searchStr,
            ignoreCase = true,
        )
    }
    return filteredItems
}