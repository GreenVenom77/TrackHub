package com.trackhub.feat_notifications.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.SuccessDialog
import com.greenvenom.core_ui.components.WarningDialog
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.theme.AppTheme
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_notifications.domain.models.HubInvitation
import com.trackhub.feat_notifications.R
import com.trackhub.feat_notifications.presentation.NotificationsAction
import com.trackhub.feat_notifications.presentation.NotificationsState
import com.trackhub.feat_notifications.presentation.components.HubInvitationItem
import com.trackhub.feat_notifications.presentation.mappers.toUI
import com.trackhub.feat_notifications.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    navigateBack: () -> Unit
) {
    BaseScreen<NotificationsViewModel>(
        onPhysicalBack = {
            navigateBack()
        }
    ) { viewModel ->
        val notificationsState by viewModel.notificationsState.collectAsStateWithLifecycle()

        NotificationsScreenContent(
            notificationsState = notificationsState,
            notificationsAction = viewModel::notificationsAction,
            baseAction = viewModel::baseAction
        )
    }
}

@Composable
private fun NotificationsScreenContent(
    notificationsState: NotificationsState,
    notificationsAction: (NotificationsAction) -> Unit,
    baseAction: (BaseAction) -> Unit
) {
    val context = LocalContext.current

    var showWarningDialog by rememberSaveable { mutableStateOf(false) }
    var warningAction by rememberSaveable { mutableStateOf<(() -> Unit)?>(null) }
    var warningMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }

    notificationsState.invitationAcceptanceResult
        ?.onSuccess { acceptance ->
            if (acceptance.success) {
                successMessage = stringResource(acceptance.message)
                showSuccessDialog = true
            } else {
                baseAction(BaseAction.ShowErrorMessage(
                    stringResource(acceptance.message),
                    dismissAction = {
                        notificationsAction(NotificationsAction.ClearNetworkOperations)
                    }
                ))
            }
        }
        ?.onError { error ->
            baseAction(BaseAction.ShowErrorMessage(
                stringResource(error.messageId),
                dismissAction = {
                    notificationsAction(NotificationsAction.ClearNetworkOperations)
                }
            ))
        }

    notificationsState.invitationsFetchingResult
        ?.onError { error ->
            baseAction(BaseAction.ShowErrorMessage(
                stringResource(error.messageId),
                dismissAction = {
                    notificationsAction(NotificationsAction.ClearNetworkOperations)
                }
            ))
        }

    SetScaffold()

    if (notificationsState.invitations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                Text(
                    text = stringResource(R.string.no_invitations_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.no_invitations_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = notificationsState.invitations,
                key = { invitation -> invitation.invitationId }
            ) { invitation ->
                HubInvitationItem(
                    invitation = invitation.toUI(),
                    onAccept = {
                        notificationsAction(
                            NotificationsAction.RespondToInvitation(
                                invitation.invitationId,
                                true
                            )
                        )
                    },
                    onReject = {
                        warningAction = {
                            notificationsAction(
                                NotificationsAction.RespondToInvitation(
                                    invitation.invitationId,
                                    false
                                )
                            )
                        }
                        warningMessage = context.getString(R.string.reject_invitation_warning)
                        showWarningDialog = true
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    WarningDialog(
        showDialog = showWarningDialog,
        warningMessage = warningMessage,
        onDismiss = {
            showWarningDialog = false
            warningAction = null
            warningMessage = null
        },
        onConfirm = {
            warningAction?.invoke()
        },
    )

    SuccessDialog(
        showDialog = showSuccessDialog,
        successMessage = successMessage,
        onDismiss = {
            notificationsAction(NotificationsAction.ClearNetworkOperations)
            showSuccessDialog = false
            successMessage = null
        }
    )
}

@Preview(
    name = "Hub Invitations Screen - Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Hub Invitations Screen - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HubInvitationsScreenPreview() {
    AppTheme {
        NotificationsScreenContent(
            notificationsState = NotificationsState(
                invitations = listOf(
                    HubInvitation(
                        invitationId = 1,
                        hubId = "123L",
                        hubName = "Product Design Team",
                        hubDescription = "Collaborate on design projects and share creative resources",
                        inviterName = "Sarah Johnson",
                        inviterEmail = "sarah.johnson@company.com",
                        hubRole = HubRole.Editor,
                        createdAt = "2024-12-05T14:30:00Z"
                    ),
                    HubInvitation(
                        invitationId = 2,
                        hubId = "456L",
                        hubName = "Engineering Hub",
                        hubDescription = "Development team for mobile and backend services",
                        inviterName = "Michael Chen",
                        inviterEmail = "m.chen@tech.com",
                        hubRole = HubRole.Editor,
                        createdAt = "2024-12-04T14:30:00Z"
                    ),
                    HubInvitation(
                        invitationId = 3,
                        hubId = "789L",
                        hubName = "Marketing Campaign 2025",
                        hubDescription = "No description available",
                        inviterName = "Emma Williams",
                        inviterEmail = "emma.w@marketing.io",
                        hubRole = HubRole.Viewer,
                        createdAt = "2024-12-03T14:30:00Z"
                    )
                )
            ),
            notificationsAction = {  },
            baseAction = {  },
        )
    }
}

@Preview(
    name = "Hub Invitations Screen Empty - Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Hub Invitations Screen Empty - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HubInvitationsScreenEmptyPreview() {
    AppTheme {
        NotificationsScreenContent(
            notificationsState = NotificationsState(
                invitations = emptyList()
            ),
            notificationsAction = {  },
            baseAction = {  },
        )
    }
}