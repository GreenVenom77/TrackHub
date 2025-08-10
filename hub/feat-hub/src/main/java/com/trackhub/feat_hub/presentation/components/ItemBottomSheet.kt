package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.components.CustomButton
import com.greenvenom.core_ui.components.CustomTextField
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.HubItemUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    modifier: Modifier = Modifier,
    hubItem: HubItemUI? = null,
    onAdd: (String, String, String) -> Unit = {_,_,_ ->},
    onEdit: (String, String, String) -> Unit = {_,_,_ ->},
    onDelete: (Int) -> Unit = {},
) {
    ItemSheetContent(
        sheetState = sheetState,
        onDismiss = onDismiss,
        isEdit = isEdit,
        modifier = modifier,
        itemId = hubItem?.id ?: -1,
        itemName = hubItem?.name ?: "",
        itemStock = hubItem?.stockCount ?: "",
        itemUnit = hubItem?.unit ?: "",
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemSheetContent(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    modifier: Modifier = Modifier,
    itemId: Int = -1,
    itemName: String = "",
    itemStock: String = "",
    itemUnit: String = "",
    onAdd: (String, String, String ) -> Unit,
    onEdit: (String, String, String) -> Unit,
    onDelete: (Int) -> Unit,
) {
    var newItemName by remember { mutableStateOf(itemName) }
    var newItemStock by remember { mutableStateOf(itemStock) }
    var newItemUnit by remember { mutableStateOf(itemUnit) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
            newItemName = ""
            newItemStock = ""
            newItemUnit = ""
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isEdit) stringResource(R.string.update_item) else stringResource(R.string.add_item),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
//            if (isEdit) {
//                CustomTextField(
//                    value = itemId.toString(),
//                    onValueChange = {},
//                    label = "ID",
//                    error = "",
//                    readOnly = true
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
            CustomTextField(
                value = newItemName,
                label = stringResource(R.string.item_name),
                error = "",
                onValueChange = { newItemName = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = newItemStock,
                label = stringResource(R.string.item_stock),
                error = "",
                onValueChange = { newItemStock = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(
                value = newItemUnit,
                label = stringResource(R.string.item_unit),
                error = "",
                onValueChange = { newItemUnit = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(40.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = stringResource(
                        if (isEdit) R.string.update_item else R.string.add_item
                    ),
                    onClick = {
                        if (isEdit) {
                            onEdit(newItemName, newItemStock, newItemUnit)
                        } else {
                            onAdd(newItemName, newItemStock, newItemUnit)
                        }
                    },
                    enabled = newItemName.isNotEmpty()
                            && newItemStock.isNotEmpty()
                            && newItemUnit.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                if (isEdit) {
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomButton(
                        text = stringResource(R.string.delete_item),
                        onClick = {
                            onDelete(itemId)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        enabled = itemId != -1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}