package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.components.CustomButton
import com.greenvenom.core_ui.components.CustomMultilineTextField
import com.greenvenom.core_ui.components.CustomTextField
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.models.HubUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HubBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    modifier: Modifier = Modifier,
    isDismissible: Boolean = true,
    hub: HubUI? = null,
    onAdd: (String, String) -> Unit = {_,_ ->},
    onEdit: (String, String) -> Unit = {_,_ ->},
    onDelete: (String) -> Unit = {},
) {
    HubSheetContent(
        sheetState = sheetState,
        onDismiss = onDismiss,
        isEdit = isEdit,
        isDismissible = isDismissible,
        modifier = modifier,
        hubId = hub?.id ?: "",
        hubName = hub?.name ?: "",
        hubDescription = hub?.description ?: "",
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HubSheetContent(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    isEdit: Boolean,
    isDismissible: Boolean,
    modifier: Modifier = Modifier,
    hubId: String = "",
    hubName: String = "",
    hubDescription: String = "",
    onAdd: (String, String) -> Unit,
    onEdit: (String, String) -> Unit,
    onDelete: (String) -> Unit,
) {
    var newHubName by remember { mutableStateOf(hubName) }
    var newHubDescription by remember { mutableStateOf(hubDescription) }
    var isDeletePressed by remember { mutableStateOf(false) }
    var isEditPressed by remember { mutableStateOf(false) }

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
                newHubName = ""
                newHubDescription = ""
                isDeletePressed = false
                isEditPressed = false
            }
        },
        dragHandle = {
            if (isDismissible) { BottomSheetDefaults.DragHandle() }
        },
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isEdit) stringResource(R.string.update_hub) else stringResource(R.string.add_hub),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = newHubName,
                label = stringResource(R.string.hub_name),
                onValueChange = { newHubName = it },
                imeAction = ImeAction.Next,
                isRequired = true,
                readOnly = isEditPressed || isDeletePressed
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomMultilineTextField(
                value = newHubDescription,
                placeholder = stringResource(R.string.hub_description),
                onValueChange = { newHubDescription = it },
                readOnly = isEditPressed || isDeletePressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = stringResource(
                        if (isEdit) R.string.update_hub else R.string.add_hub
                    ),
                    onClick = {
                        isEditPressed = true
                        if (isEdit) {
                            onEdit(newHubName, newHubDescription)
                        } else {
                            onAdd(newHubName, newHubDescription)
                        }
                    },
                    enabled = newHubName.isNotEmpty() && !isDeletePressed,
                    modifier = Modifier.weight(1f)
                )
                if (isEdit) {
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomButton(
                        text = stringResource(R.string.delete_hub),
                        onClick = {
                            isDeletePressed = true
                            onDelete(hubId)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        enabled = hubId.isNotEmpty() && !isEditPressed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}