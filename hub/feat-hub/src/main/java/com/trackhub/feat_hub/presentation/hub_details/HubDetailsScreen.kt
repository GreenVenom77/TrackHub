package com.trackhub.feat_hub.presentation.hub_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.buttons.FloatingButton
import com.greenvenom.core_ui.components.buttons.OptionsDropdownMenu
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.components.AboutHubSheet
import com.trackhub.feat_hub.presentation.components.FilterDropdownRow
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.InviteUserDialog
import com.trackhub.feat_hub.presentation.components.ItemBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemDetailsDialog
import com.trackhub.feat_hub.presentation.components.ItemListCard
import com.trackhub.feat_hub.presentation.mappers.toHubItemUI
import com.trackhub.feat_hub.presentation.mappers.toHubUI
import com.trackhub.feat_hub.presentation.mappers.toUI
import com.trackhub.feat_hub.presentation.models.HubUI
import com.trackhub.feat_hub.presentation.models.ItemUI
import kotlinx.coroutines.launch

@Composable
fun HubDetailsScreen(
    navigateBack: () -> Unit
) {
    BaseScreen<HubDetailsViewModel>(
        onPhysicalBack = {
            navigateBack()
        }
    ) { viewModel ->
        val hubDetailsState by viewModel.hubDetailsState.collectAsStateWithLifecycle()

        HubDetailsContent(
            hubDetailsState = hubDetailsState,
            hubDetailsAction = {
                when(it) {
                    is HubDetailsAction.NavigateBack -> navigateBack()
                }
                viewModel.hubDetailsAction(it)
            },
            baseAction = viewModel::baseAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HubDetailsContent(
    hubDetailsState: HubDetailsState,
    hubDetailsAction: (HubDetailsAction) -> Unit,
    baseAction: (BaseAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val resource = LocalResources.current

    val lazyPagingItems = hubDetailsState.items.collectAsLazyPagingItems()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isItemEdit by rememberSaveable { mutableStateOf(false) }
    var hubSheetState by rememberSaveable { mutableStateOf(false) }
    var aboutHubSheetState by rememberSaveable { mutableStateOf(false) }
    var itemDetailsState by rememberSaveable { mutableStateOf(false) }
    var showInviteDialog by rememberSaveable { mutableStateOf(false) }

    hubDetailsState.hubItemsResult
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    stringResource(error.messageId)
                )
            )
        }

    hubDetailsState.operationResult
        ?.onSuccess {
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
            scope.launch {
                sheetState.hide()
                itemDetailsState = false
                isItemEdit = false
            }
            baseAction(BaseAction.ShowSuccessDialog())
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    errorMessage = stringResource(error.messageId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    }
                )
            )
        }

    hubDetailsState.hubUpdateResult
        ?.onSuccess {
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            if (hubSheetState) {
                scope.launch {
                    sheetState.hide()
                    hubSheetState = false
                }
            }
            baseAction(BaseAction.ShowSuccessDialog())
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    errorMessage = stringResource(error.messageId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    }
                )
            )
        }

    hubDetailsState.hubDeletionResult
        ?.onSuccess {
            scope.launch {
                sheetState.hide()
                hubSheetState = false
            }
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.NavigateBack)
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    errorMessage = stringResource(error.messageId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    }
                )
            )
        }

    hubDetailsState.itemDeletionResult
        ?.onSuccess {
            scope.launch {
                sheetState.hide()
                itemDetailsState = false
                isItemEdit = false
            }
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
            baseAction(BaseAction.ShowSuccessDialog())
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                }
            ))
        }

    hubDetailsState.invitationProcessResult
        ?.onSuccess { result ->
            if (result.success) {
                hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                showInviteDialog = false
                baseAction(BaseAction.ShowSuccessDialog(
                    successMessage = stringResource(result.toUI().messageResId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    }
                ))
            } else {
                baseAction(
                    BaseAction.ShowErrorMessage(
                        errorMessage = stringResource(result.toUI().messageResId),
                        dismissAction = {
                            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                        }
                    )
                )
            }
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    errorMessage = stringResource(error.messageId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    }
                )
            )
        }

    SetScaffold(
        title = hubDetailsState.hub?.name ?: stringResource(com.greenvenom.core_ui.R.string.app_name),
        showLogo = true,
        showSearchBar = true,
        onSearchQueryChange = { searchQuery ->
            hubDetailsAction(HubDetailsAction.SearchItems(
                searchQuery = searchQuery
            ))
        },
        navigateBackAction = { hubDetailsAction(HubDetailsAction.NavigateBack) },
        topBarActions = {
            OptionsDropdownMenu { onDismiss ->
                when (hubDetailsState.hub?.role) {
                    HubRole.Owner -> {
                        DropdownMenuItem(
                            onClick = {
                                hubDetailsAction(HubDetailsAction.GetAllInvitations)
                                onDismiss()
                                aboutHubSheetState = true
                            },
                            text = { Text(
                                text = stringResource(R.string.about_hub),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(R.string.about_hub),
                                )
                            }
                        )

                        DropdownMenuItem(
                            onClick = {
                                onDismiss()
                                showInviteDialog = true
                            },
                            text = { Text(
                                text = stringResource(R.string.invite),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = stringResource(R.string.invite),
                                )
                            }
                        )

                        DropdownMenuItem(
                            onClick = {
                                hubDetailsAction(HubDetailsAction.GetAllInvitations)
                                onDismiss()
                                hubSheetState = true
                            },
                            text = { Text(
                                stringResource(R.string.settings),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            ) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.settings_ic),
                                    contentDescription = stringResource(R.string.edit_hub_Details),
                                )
                            }
                        )
                    }
                    HubRole.Editor -> {
                        DropdownMenuItem(
                            onClick = {
                                hubDetailsAction(HubDetailsAction.GetAllInvitations)
                                onDismiss()
                                aboutHubSheetState = true
                            },
                            text = { Text(
                                text = stringResource(R.string.about_hub),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(R.string.about_hub),
                                )
                            }
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            onClick = {
                                onDismiss()
                                baseAction(BaseAction.ShowWarningDialog(
                                    warningMessage = resource.getString(R.string.leave_hub_message),
                                    confirmAction = {
                                        hubDetailsAction(HubDetailsAction.LeaveHub)
                                    }
                                ))
                            },
                            text = { Text(
                                text = stringResource(R.string.leave_hub),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = stringResource(R.string.leave_hub),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                    else -> {
                        DropdownMenuItem(
                            onClick = {
                                hubDetailsAction(HubDetailsAction.GetAllInvitations)
                                onDismiss()
                                aboutHubSheetState = true
                            },
                            text = { Text(
                                text = stringResource(R.string.about_hub),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(R.string.about_hub),
                                )
                            }
                        )

                        HorizontalDivider()

                        DropdownMenuItem(
                            onClick = {
                                onDismiss()
                                baseAction(BaseAction.ShowWarningDialog(
                                    warningMessage = resource.getString(R.string.leave_hub_message),
                                    confirmAction = { hubDetailsAction(HubDetailsAction.LeaveHub) }
                                ))
                            },
                            text = { Text(
                                text = stringResource(R.string.leave_hub),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            ) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = stringResource(R.string.leave_hub),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        },

        floatingActionButton = {
            if (hubDetailsState.hub?.role == HubRole.Owner
                || hubDetailsState.hub?.role == HubRole.Editor) {
                FloatingButton(
                    isVisible = true,
                    onClick = {
                        isItemEdit = false
                        hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
                        itemDetailsState = true
                    }
                )
            }
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter Component
        FilterDropdownRow(
            categories = hubDetailsState.hub?.categoryList ?: emptyList(),
            manufacturers = hubDetailsState.hub?.manufacturerList ?: emptyList(),
            defaultCategory = stringResource(R.string.all_categories),
            selectedCategory = hubDetailsState.selectedCategory ?: stringResource(R.string.all_categories),
            defaultManufacturer = stringResource(R.string.all_manufacturers),
            selectedManufacturer = hubDetailsState.selectedManufacturer ?: stringResource(R.string.all_manufacturers),
            selectedInStock = hubDetailsState.selectedInStock,
            onCategorySelected = { category ->
                hubDetailsAction(HubDetailsAction.FilterItems(
                    category = category,
                    manufacturer = hubDetailsState.selectedManufacturer,
                    inStock = hubDetailsState.selectedInStock
                ))
            },
            onManufacturerSelected = { manufacturer ->
                hubDetailsAction(HubDetailsAction.FilterItems(
                    category = hubDetailsState.selectedCategory,
                    manufacturer = manufacturer,
                    inStock = hubDetailsState.selectedInStock
                ))
            },
            onInStockSelected = { inStock ->
                hubDetailsAction(HubDetailsAction.FilterItems(
                    category = hubDetailsState.selectedCategory,
                    manufacturer = hubDetailsState.selectedManufacturer,
                    inStock = inStock
                ))
            }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Items List
        PullToRefreshBox(
            isRefreshing = hubDetailsState.isRefreshing,
            onRefresh = {
                hubDetailsAction(HubDetailsAction.RefreshHub)
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            lazyPagingItems.takeIf { it.itemCount > 0 }?.let { hubItems ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(
                        count = hubItems.itemCount,
                        key = hubItems.itemKey { item -> item.id },
                        contentType = lazyPagingItems.itemContentType { "Items" }
                    ) { index ->
                        val hubItem = lazyPagingItems[index] as Item
                        ItemListCard(
                            hubItem = hubItem.toHubItemUI(),
                            onClick = {
                                isItemEdit = true
                                hubDetailsAction(HubDetailsAction.ChangeCurrentItem(hubItem))
                                itemDetailsState = true
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .animateItem()
                        )
                    }
                }
            } ?: LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        Text(
                            text = stringResource(R.string.no_items_found),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = stringResource(R.string.no_items_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }
        }

        if (hubSheetState) {
            hubDetailsState.hub?.let { hub ->
                HubBottomSheet(
                    hub = hub.toHubUI(),
                    sheetState = sheetState,
                    hubMembers = hubDetailsState.invitationsList,
                    onDismiss = { hubSheetState = false },
                    isEdit = true,
                    onEdit = { hubName, hubDescription ->
                        hubDetailsAction(HubDetailsAction.UpdateHub(
                            hub.copy(
                                name = hubName,
                                description = hubDescription
                            )
                        ))
                    },
                    onDelete = { hubId ->
                        baseAction(BaseAction.ShowWarningDialog(
                            confirmAction = {
                                hubDetailsAction(HubDetailsAction.DeleteHub(hubId))
                            }
                        ))
                    },
                    onShowInviteDialog = { showInviteDialog = true },
                    onRemoveMember = { userId, userStatus ->
                        baseAction(BaseAction.ShowWarningDialog(
                            warningMessage = resource.getString(R.string.remove_member_message),
                            confirmAction = {
                                hubDetailsAction(HubDetailsAction.RemoveMember(userId, userStatus))
                            }
                        ))
                    },
                    onChangeRole = { userId, role, userStatus ->
                        hubDetailsAction(HubDetailsAction.ChangeMemberRole(userId, role, userStatus))
                    }
                )
            }
        }

        if (aboutHubSheetState) {
            AboutHubSheet(
                sheetState = sheetState,
                onDismiss = { aboutHubSheetState = false },
                hub = hubDetailsState.hub?.toHubUI() as HubUI,
                hubMembers = hubDetailsState.invitationsList
            )
        }

        if ((hubDetailsState.hub?.role == HubRole.Owner
                    || hubDetailsState.hub?.role == HubRole.Editor)
            && itemDetailsState) {
            ItemBottomSheet(
                sheetState = sheetState,
                isEdit = isItemEdit,
                hubItem = hubDetailsState.currentItem?.toHubItemUI(),
                manufacturers = hubDetailsState.hub.manufacturerList,
                categories = hubDetailsState.hub.categoryList,
                onDismiss = {
                    itemDetailsState = false
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
                },
                onAdd = { newItem ->
                    hubDetailsAction(
                        HubDetailsAction.AddItem(
                            newItem
                        )
                    )
                },
                onEdit = { updatedItem ->
                    hubDetailsAction(
                        HubDetailsAction.UpdateItem(
                            updatedItem
                        )
                    )
                },
                onDelete = { itemId ->
                    baseAction(BaseAction.ShowWarningDialog(
                        confirmAction = { hubDetailsAction(HubDetailsAction.DeleteItem(itemId)) },
                        isDeletionDialog = true
                    ))
                },
                onAddManufacturer = { manufacturerName ->
                    hubDetailsAction(HubDetailsAction.UpdateHub(
                        hubDetailsState.hub.copy(
                            manufacturerList = hubDetailsState.hub.manufacturerList.plus(
                                manufacturerName
                            )
                        )
                    ))
                },
                onAddCategory = { categoryName ->
                    hubDetailsAction(HubDetailsAction.UpdateHub(
                        hubDetailsState.hub.copy(
                            categoryList = hubDetailsState.hub.categoryList.plus(
                                categoryName
                            )
                        )
                    ))
                }
            )
        } else if (hubDetailsState.hub?.role == HubRole.Viewer && itemDetailsState) {
            ItemDetailsDialog(
                onDismiss = {
                    itemDetailsState = false
                },
                item = hubDetailsState.currentItem?.toHubItemUI() as ItemUI
            )
        }

        // Invite User Dialog
        if (showInviteDialog) {
            InviteUserDialog(
                foundUsers = hubDetailsState.usersList,
                onDismiss = { showInviteDialog = false },
                onClearList = { hubDetailsAction(HubDetailsAction.ClearUserSearch) },
                onSearchUsers = { searchTerm ->
                    hubDetailsAction(HubDetailsAction.SearchForUsers(searchTerm))
                },
                onInvite = { userId, role ->
                    hubDetailsAction(HubDetailsAction.InviteUser(userId, role))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HubDetailsContentPreview() {
    HubDetailsContent(
        hubDetailsState = HubDetailsState(),
        hubDetailsAction = {  },
        baseAction = {  }
    )
}