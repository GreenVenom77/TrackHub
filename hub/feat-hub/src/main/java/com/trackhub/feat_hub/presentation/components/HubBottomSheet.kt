package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.components.CustomButton
import com.greenvenom.core_ui.components.CustomMultilineTextField
import com.greenvenom.core_ui.components.CustomTextField
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.UserSearch
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
    hubMembers: List<HubMember> = emptyList(),
    foundUsers: List<UserSearch> = emptyList(),
    onAdd: (String, String) -> Unit = { _, _ -> },
    onEdit: (String, String) -> Unit = { _, _ -> },
    onDelete: (String) -> Unit = {},
    onSearchUsers: (String) -> Unit = {},
    onInviteUser: (String, HubRole) -> Unit = { _, _ -> },
    onRemoveMember: (String) -> Unit = {},
    onChangeRole: (String, HubRole) -> Unit = { _, _ -> },
) {
    HubSheetContent(
        sheetState = sheetState,
        onDismiss = onDismiss,
        isEdit = isEdit,
        isDismissible = isDismissible,
        modifier = modifier,
        hub = hub,
        hubMembers = hubMembers,
        foundUsers = foundUsers,
        onAdd = onAdd,
        onEdit = onEdit,
        onDelete = onDelete,
        onSearchUsers = onSearchUsers,
        onInviteUser = onInviteUser,
        onRemoveMember = onRemoveMember,
        onChangeRole = onChangeRole
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
    hub: HubUI? = null,
    hubMembers: List<HubMember> = emptyList(),
    foundUsers: List<UserSearch> = emptyList(),
    onAdd: (String, String) -> Unit,
    onEdit: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onSearchUsers: (String) -> Unit,
    onInviteUser: (String, HubRole) -> Unit,
    onRemoveMember: (String) -> Unit,
    onChangeRole: (String, HubRole) -> Unit,
) {
    var newHubName by remember { mutableStateOf(hub?.name ?: "") }
    var newHubDescription by remember { mutableStateOf(hub?.description ?: "") }
    var isDeletePressed by remember { mutableStateOf(false) }
    var isEditPressed by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            if (isDismissible) {
                onDismiss()
            }
        },
        dragHandle = {
            if (isDismissible) {
                BottomSheetDefaults.DragHandle()
            }
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
                    text = if (isEdit) stringResource(R.string.update_hub) else stringResource(R.string.add_hub),
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
                    // Hub Name Field
                    FormFieldWithIcon(
                        label = stringResource(R.string.hub_name),
                        icon = Icons.Default.Hub
                    ) {
                        CustomTextField(
                            value = newHubName,
                            label = null,
                            onValueChange = { newHubName = it },
                            imeAction = ImeAction.Next,
                            readOnly = isDeletePressed || isEditPressed,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Hub Description Field
                    FormFieldWithIcon(
                        label = stringResource(R.string.hub_description),
                        icon = Icons.Default.Description
                    ) {
                        CustomMultilineTextField(
                            value = newHubDescription,
                            placeholder = stringResource(R.string.hub_description_hint),
                            onValueChange = { newHubDescription = it },
                            readOnly = isDeletePressed || isEditPressed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(12.dp)
                        )
                    }

                    if (isEdit && hub?.createdAt != null) {
                        FormFieldWithIcon(
                            label = stringResource(R.string.created_at),
                            icon = Icons.Default.CalendarToday
                        ) {
                            CustomTextField(
                                value = hub.createdAt,
                                label = null,
                                onValueChange = { },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Members Section (Only in Edit Mode)
            if (isEdit) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Members Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.members),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            TextButton(
                                onClick = { showInviteDialog = true },
                                enabled = !isDeletePressed && !isEditPressed
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.invite))
                            }
                        }

                        // Members List
                        if (hubMembers.isEmpty()) {
                            Text(
                                text = stringResource(R.string.no_members_yet),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            hubMembers.forEach { member ->
                                MemberItem(
                                    member = member,
                                    onRemove = { onRemoveMember(member.userId) },
                                    onChangeRole = { newRole -> onChangeRole(member.userId, newRole) },
                                    enabled = !isDeletePressed && !isEditPressed
                                )
                            }
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
                        if (isEdit) R.string.update_hub else R.string.add_hub
                    ),
                    onClick = {
                        if (isEdit) {
                            onEdit(newHubName, newHubDescription)
                        } else {
                            onAdd(newHubName, newHubDescription)
                        }
                    },
                    enabled = newHubName.isNotEmpty() && !isDeletePressed,
                    isLoading = isEditPressed,
                    modifier = Modifier.weight(1f)
                )

                if (isEdit) {
                    CustomButton(
                        text = stringResource(R.string.delete_hub),
                        onClick = {
                            hub?.id?.let { onDelete(it) }
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        enabled = hub != null && !isEditPressed,
                        isLoading = isDeletePressed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Invite User Dialog
    if (showInviteDialog) {
        InviteUserDialog(
            foundUsers = foundUsers,
            onDismiss = { showInviteDialog = false },
            onSearchUsers = onSearchUsers,
            onInvite = { userId, role ->
                onInviteUser(userId, role)
                showInviteDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Add Hub")
@Composable
private fun PreviewAddHub() {
    MaterialTheme {
        HubBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = {},
            isEdit = false,
            isDismissible = true,
            hub = null,
            hubMembers = emptyList(),
            onAdd = { _, _ -> },
            onEdit = { _, _ -> },
            onDelete = {},
            onSearchUsers = {},
            onInviteUser = { _, _ -> },
            onRemoveMember = {},
            onChangeRole = { _, _ -> }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Edit Hub - No Members")
@Composable
private fun PreviewEditHubNoMembers() {
    MaterialTheme {
        HubBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = {},
            isEdit = true,
            isDismissible = true,
            hub = HubUI(
                id = "hub_1",
                userId = "user_1",
                name = "Main Warehouse",
                description = "Central storage facility for all products",
                createdAt = "2023-01-01",
                role = HubRole.Owner.ordinal
            ),
            hubMembers = emptyList(),
            onAdd = { _, _ -> },
            onEdit = { _, _ -> },
            onDelete = {},
            onSearchUsers = {},
            onInviteUser = { _, _ -> },
            onRemoveMember = {},
            onChangeRole = { _, _ -> }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Edit Hub - With Members")
@Composable
private fun PreviewEditHubWithMembers() {
    MaterialTheme {
        HubBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismiss = {},
            isEdit = true,
            isDismissible = true,
            hub = HubUI(
                id = "hub_1",
                userId = "user_1",
                name = "Main Warehouse",
                description = "Central storage facility for all products",
                createdAt = "2023-01-01",
                role = HubRole.Owner.ordinal
            ),
            hubMembers = listOf(
                HubMember(
                    userId = "user_1",
                    name = "John Doe",
                    email = "john@example.com",
                    role = HubRole.Owner,
                    status = MemberStatus.Member
                ),
                HubMember(
                    userId = "user_2",
                    name = "Jane Smith",
                    email = "jane@example.com",
                    role = HubRole.Editor,
                    status = MemberStatus.Member
                ),
                HubMember(
                    userId = "user_3",
                    name = "Bob Johnson",
                    email = "bob@example.com",
                    role = HubRole.Viewer,
                    status = MemberStatus.PendingInvitation
                )
            ),
            onAdd = { _, _ -> },
            onEdit = { _, _ -> },
            onDelete = {},
            onSearchUsers = {},
            onInviteUser = { _, _ -> },
            onRemoveMember = {},
            onChangeRole = { _, _ -> }
        )
    }
}