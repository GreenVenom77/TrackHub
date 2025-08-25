package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.components.CustomButton
import com.greenvenom.core_ui.components.CustomTextField
import com.greenvenom.core_ui.components.DialogSearchableDropdown
import com.greenvenom.core_ui.theme.AppTheme
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.ItemUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    modifier: Modifier = Modifier,
    isDismissible: Boolean = true,
    hubItem: ItemUI? = null,
    categories: List<String> = emptyList(),
    manufacturers: List<String> = emptyList(),
    onAdd: (Item) -> Unit = {},
    onEdit: (Item) -> Unit = {},
    onDelete: (Int) -> Unit = {},
    onAddCategory: (String) -> Unit = {},
    onAddManufacturer: (String) -> Unit = {},
) {
    ItemSheetContent(
        sheetState = sheetState,
        onDismiss = onDismiss,
        isEdit = isEdit,
        isDismissible = isDismissible,
        modifier = modifier,
        hubItem = hubItem,
        categories = categories,
        manufacturers = manufacturers,
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = onDelete,
        onAddCategory = onAddCategory,
        onAddManufacturer = onAddManufacturer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemSheetContent(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    isDismissible: Boolean,
    modifier: Modifier = Modifier,
    hubItem: ItemUI? = null,
    categories: List<String> = emptyList(),
    manufacturers: List<String> = emptyList(),
    onAdd: (Item) -> Unit,
    onEdit: (Item) -> Unit,
    onDelete: (Int) -> Unit,
    onAddCategory: (String) -> Unit,
    onAddManufacturer: (String) -> Unit,
) {
    var newItemName by remember { mutableStateOf(hubItem?.name ?: "") }
    var newItemStock by remember { mutableStateOf(hubItem?.stockCount ?: "") }
    var newItemUnit by remember { mutableStateOf(hubItem?.unit ?: "") }
    var selectedCategory by remember { mutableStateOf(hubItem?.category) }
    var selectedManufacturer by remember { mutableStateOf(hubItem?.manufacturer) }
    var isDeletePressed by remember { mutableStateOf(false) }
    var isEditPressed by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddManufacturerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isDismissible) {
        if (isEditPressed || isDeletePressed) {
            isDeletePressed = false
            isEditPressed = false
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (isDismissible) {
                onDismiss()
                newItemName = ""
                newItemStock = ""
                newItemUnit = ""
                selectedCategory = null
                selectedManufacturer = null
                isDeletePressed = false
                isEditPressed = false
            }
        },
        dragHandle = {
            if (isDismissible) { BottomSheetDefaults.DragHandle() }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isEdit) stringResource(R.string.update_item) else stringResource(R.string.add_item),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = newItemName,
                label = stringResource(R.string.item_name),
                onValueChange = { newItemName = it },
                imeAction = ImeAction.Next,
                readOnly = isDeletePressed || isEditPressed
            )

            CustomTextField(
                value = newItemStock,
                label = stringResource(R.string.item_stock),
                onValueChange = { newItemStock = it },
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
                readOnly = isDeletePressed || isEditPressed
            )

            CustomTextField(
                value = newItemUnit,
                label = stringResource(R.string.item_unit),
                onValueChange = { newItemUnit = it },
                imeAction = ImeAction.Next,
                readOnly = isDeletePressed || isEditPressed
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.category),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showAddCategoryDialog = true },
                        enabled = !isDeletePressed && !isEditPressed
                    ) {
                        Text(stringResource(R.string.add_new))
                    }
                }

                DialogSearchableDropdown(
                    selectedOption = selectedCategory,
                    onItemSelected = { selectedCategory = it },
                    options = categories,
                    placeholder = stringResource(R.string.select_category),
                    enabled = !isDeletePressed && !isEditPressed
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.manufacturer),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { showAddManufacturerDialog = true },
                        enabled = !isDeletePressed && !isEditPressed
                    ) {
                        Text(stringResource(R.string.add_new))
                    }
                }

                DialogSearchableDropdown(
                    selectedOption = selectedManufacturer,
                    onItemSelected = { selectedManufacturer = it },
                    options = manufacturers,
                    placeholder = stringResource(R.string.select_manufacturer),
                    enabled = !isDeletePressed && !isEditPressed
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = stringResource(
                        if (isEdit) R.string.update_item else R.string.add_item
                    ),
                    onClick = {
                        val item = Item(
                            id = hubItem?.id ?: 0,
                            hubId = hubItem?.hubId ?: "",
                            name = newItemName,
                            stockCount = newItemStock.toFloat(),
                            unit = newItemUnit,
                            imageUrl = hubItem?.imageUrl,
                            createdAt = hubItem?.createdAt ?: "",
                            updatedAt = hubItem?.updatedAt,
                            manufacturer = selectedManufacturer,
                            category = selectedCategory,
                            inStock = hubItem?.inStock ?: true
                        )

                        if (isEdit) {
                            onEdit(item)
                        } else {
                            onAdd(item)
                        }
                    },
                    enabled = newItemName.isNotEmpty()
                            && newItemStock.isNotEmpty()
                            && newItemUnit.isNotEmpty()
                            && !isDeletePressed,
                    isLoading = isEditPressed,
                    modifier = Modifier.weight(1f)
                )
                if (isEdit) {
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomButton(
                        text = stringResource(R.string.delete_item),
                        onClick = {
                            hubItem?.let { onDelete(it.id) }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        enabled = hubItem != null && !isEditPressed,
                        isLoading = isDeletePressed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_category),
            placeholder = stringResource(R.string.category_name),
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { categoryName ->
                if (categoryName.isNotBlank()) {
                    onAddCategory(categoryName.trim())
                    selectedCategory = categoryName.trim()
                }
                showAddCategoryDialog = false
            }
        )
    }

    // Add Manufacturer Dialog
    if (showAddManufacturerDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_manufacturer),
            placeholder = stringResource(R.string.manufacturer_name),
            onDismiss = { showAddManufacturerDialog = false },
            onConfirm = { manufacturerName ->
                if (manufacturerName.isNotBlank()) {
                    onAddManufacturer(manufacturerName.trim())
                    selectedManufacturer = manufacturerName.trim()
                }
                showAddManufacturerDialog = false
            }
        )
    }
}

@Composable
private fun AddItemDialog(
    title: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            CustomTextField(
                value = text,
                label = placeholder,
                onValueChange = { text = it },
                imeAction = ImeAction.Done
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun SheetContentPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ItemSheetContent(
                sheetState = rememberStandardBottomSheetState(SheetValue.Expanded),
                onDismiss = {},
                onAdd = {},
                onEdit = {},
                onDelete = {},
                onAddCategory = {},
                onAddManufacturer = {},
                hubItem = null,
                isEdit = false,
                categories = emptyList(),
                manufacturers = emptyList(),
                isDismissible = true
            )
        }
    }
}