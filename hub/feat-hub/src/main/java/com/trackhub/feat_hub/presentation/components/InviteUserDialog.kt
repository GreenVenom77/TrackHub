package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greenvenom.core_ui.theme.AppTheme
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.core_hub.domain.models.UserSearch
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.mappers.toUI
import com.trackhub.feat_hub.presentation.utils.getRoleDescription
import kotlinx.coroutines.delay

@Composable
fun InviteUserDialog(
    foundUsers: List<UserSearch> = emptyList(),
    onDismiss: () -> Unit,
    onClearList: () -> Unit,
    onSearchUsers: (String) -> Unit,
    onInvite: (String, HubRole) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(foundUsers) }
    var selectedUser by remember { mutableStateOf<UserSearch?>(null) }
    var selectedRole by remember { mutableStateOf(HubRole.Viewer) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isSearching = true
            onClearList()
            delay(600) // Debounce delay
            onSearchUsers(searchQuery)
        } else {
            searchResults = emptyList()
            isSearching = false
        }
    }

    LaunchedEffect(foundUsers) {
        if (foundUsers.isNotEmpty()) {
            isSearching = false
            searchResults = foundUsers
        }
    }

    AlertDialog(
        onDismissRequest = { }, // Can't dismiss without cancel button
        icon = {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.invite_user),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Selected user display
                if (selectedUser != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedUser?.displayName ?: "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            IconButton(onClick = { selectedUser = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.clear_selection)
                                )
                            }
                        }
                    }
                }

                // Search field (only show if no user selected)
                if (selectedUser == null) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text(stringResource(R.string.search_by_name_or_email)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        }
                    )

                    // Search results
                    if (isSearching) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (searchResults.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            LazyColumn(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.userId }
                                ) { user ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                enabled = user.currentStatus == MemberStatus.Member
                                            ) { selectedUser = user },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        UserSearchItem(
                                            userUI = user.toUI(),
                                            modifier = Modifier.clickable {
                                                selectedUser = user
                                            }
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    } else if (searchQuery.length >= 2) {
                        Text(
                            text = stringResource(R.string.no_users_found),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Role selection (only show if user is selected)
                if (selectedUser != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.select_role),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Box {
                            OutlinedButton(
                                onClick = { isRoleDropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedRole.name)
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isRoleDropdownExpanded,
                                onDismissRequest = { isRoleDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                HubRole.entries.filter { it != HubRole.Owner }.forEach { role ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = role.name,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = stringResource(getRoleDescription(role)),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedRole = role
                                            isRoleDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedUser?.let { user ->
                        onInvite(user.userId, selectedRole)
                    }
                },
                enabled = selectedUser != null
            ) {
                Text(stringResource(R.string.send_invite))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true, name = "Invite Dialog - Initial")
@Composable
private fun PreviewInviteDialogInitial() {
    AppTheme {
        InviteUserDialog(
            onDismiss = {},
            onClearList = {},
            onSearchUsers = {  },
            onInvite = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Invite Dialog - Search Results")
@Composable
private fun PreviewInviteDialogSearchResults() {
    AppTheme {
        InviteUserDialog(
            onDismiss = {},
            onClearList = {},
            onSearchUsers = { query ->
                listOf(
                    UserSearch(
                        userId = "user_4",
                        displayName = "Alice Williams",
                        email = "alice@example.com",
                        currentStatus = MemberStatus.NotInvited
                    ),
                    UserSearch(
                        userId = "user_5",
                        displayName = "Charlie Brown",
                        email = "charlie@example.com",
                        currentStatus = MemberStatus.InvitationDeclined
                    ),
                    UserSearch(
                        userId = "user_6",
                        displayName = "David Lee",
                        email = "david@example.com",
                        currentStatus = MemberStatus.PendingInvitation
                    )
                )
            },
            onInvite = { _, _ -> }
        )
    }
}