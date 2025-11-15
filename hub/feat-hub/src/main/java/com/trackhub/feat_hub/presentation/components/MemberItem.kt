package com.trackhub.feat_hub.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.feat_hub.R

@Composable
fun MemberItem(
    member: HubMember,
    onRemove: () -> Unit,
    onChangeRole: (HubRole) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
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
                    text = member.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = member.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Badge(
                        containerColor = if (member.status == MemberStatus.Member) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    ) {
                        Text(
                            text = member.status.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Dropdown (only for non-owners)
                if (member.role != HubRole.Owner) {
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            enabled = enabled,
                            modifier = Modifier.width(120.dp)
                        ) {
                            Text(
                                text = stringResource(member.role.value),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            HubRole.entries.filter { it != HubRole.Owner }.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(role.value)) },
                                    onClick = {
                                        onChangeRole(role)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Remove button
                    IconButton(
                        onClick = onRemove,
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.remove_member),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    // Owner badge (non-clickable)
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = stringResource(HubRole.Owner.value),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Member Item - Owner")
@Composable
private fun PreviewMemberItemOwner() {
    AppTheme {
        MemberItem(
            member = HubMember(
                userId = "user_1",
                name = "John Doe",
                email = "john@example.com",
                role = HubRole.Owner,
                status = MemberStatus.Member
            ),
            onRemove = {},
            onChangeRole = {},
            enabled = true
        )
    }
}

@Preview(showBackground = true, name = "Member Item - Editor Accepted")
@Composable
private fun PreviewMemberItemEditorAccepted() {
    AppTheme {
        MemberItem(
            member = HubMember(
                userId = "user_2",
                name = "Jane Smith",
                email = "jane@example.com",
                role = HubRole.Editor,
                status = MemberStatus.Member
            ),
            onRemove = {},
            onChangeRole = {},
            enabled = true
        )
    }
}

@Preview(showBackground = true, name = "Member Item - Viewer Pending")
@Composable
private fun PreviewMemberItemViewerPending() {
    AppTheme {
        MemberItem(
            member = HubMember(
                userId = "user_3",
                name = "Bob Johnson",
                email = "bob@example.com",
                role = HubRole.Viewer,
                status = MemberStatus.PendingInvitation
            ),
            onRemove = {},
            onChangeRole = {},
            enabled = true
        )
    }
}