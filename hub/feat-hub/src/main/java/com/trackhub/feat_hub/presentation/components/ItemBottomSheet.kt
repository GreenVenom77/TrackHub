package com.trackhub.feat_hub.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (isDismissible) {
                onDismiss()
            }
        },
        dragHandle = {
            if (isDismissible) { BottomSheetDefaults.DragHandle() }
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isEdit) stringResource(R.string.update_item) else stringResource(R.string.add_item),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Content Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item Name Field
                    FormFieldWithIcon(
                        label = stringResource(R.string.item_name),
                        icon = Icons.Default.ShoppingCart
                    ) {
                        CustomTextField(
                            value = newItemName,
                            label = null,
                            onValueChange = { newItemName = it },
                            imeAction = ImeAction.Next,
                            readOnly = isDeletePressed || isEditPressed,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Stock and Unit Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormFieldWithIcon(
                            label = stringResource(R.string.item_stock),
                            icon = Icons.Default.Inventory,
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomTextField(
                                value = newItemStock,
                                label = null,
                                onValueChange = { newItemStock = it },
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                                readOnly = isDeletePressed || isEditPressed,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        FormFieldWithIcon(
                            label = stringResource(R.string.item_unit),
                            icon = Icons.Default.Scale,
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomTextField(
                                value = newItemUnit,
                                label = null,
                                onValueChange = { newItemUnit = it },
                                imeAction = ImeAction.Next,
                                readOnly = isDeletePressed || isEditPressed,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Category Field
                    FormFieldWithIcon(
                        label = stringResource(R.string.category),
                        icon = Icons.Default.Category
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showAddCategoryDialog = true },
                                    enabled = !isDeletePressed && !isEditPressed
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
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
                    }

                    // Manufacturer Field
                    FormFieldWithIcon(
                        label = stringResource(R.string.manufacturer),
                        icon = Icons.Default.Business
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showAddManufacturerDialog = true },
                                    enabled = !isDeletePressed && !isEditPressed
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
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
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                    CustomButton(
                        text = stringResource(R.string.delete_item),
                        onClick = {
                            hubItem?.let { onDelete(it.id) }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        enabled = hubItem != null && !isEditPressed,
                        isLoading = isDeletePressed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_category),
            placeholder = stringResource(R.string.category_name),
            icon = Icons.Default.Category,
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
            icon = Icons.Default.Business,
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
    icon: ImageVector,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ItemSheetContentPreview() {
    AppTheme {
        Surface {
            ItemSheetContent(
                sheetState = rememberModalBottomSheetState(),
                onDismiss = {},
                isEdit = true,
                isDismissible = true,
                hubItem = ItemUI(
                    id = 1,
                    hubId = "hub123",
                    name = "Wireless Headphones",
                    stockCount = "50",
                    unit = "pieces",
                    imageUrl = null,
                    createdAt = "2024-01-15",
                    updatedAt = "2024-01-20",
                    manufacturer = "TechCorp",
                    category = "Electronics",
                    inStock = true
                ),
                categories = listOf("Electronics", "Accessories", "Audio"),
                manufacturers = listOf("TechCorp", "AudioPro", "SoundWave"),
                onAdd = {},
                onEdit = {},
                onDelete = {},
                onAddCategory = {},
                onAddManufacturer = {}
            )
        }
    }
}