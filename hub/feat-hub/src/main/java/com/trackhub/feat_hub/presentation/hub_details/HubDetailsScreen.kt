package com.trackhub.feat_hub.presentation.hub_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.FloatingButton
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.components.FilterDropdownRow
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemListCard
import com.trackhub.feat_hub.presentation.mappers.toHubItemUI
import com.trackhub.feat_hub.presentation.mappers.toHubUI

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
    val lazyPagingItems = hubDetailsState.items.collectAsLazyPagingItems()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isItemEdit by rememberSaveable { mutableStateOf(false) }
    var hubSheetState by rememberSaveable { mutableStateOf(false) }
    var itemSheetState by rememberSaveable { mutableStateOf(false) }
    var isSheetDismissible by rememberSaveable { mutableStateOf(true) }

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
            isSheetDismissible = true
            itemSheetState = false
            isItemEdit = false
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    isSheetDismissible = true
                }
            ))
        }

    hubDetailsState.hubUpdateResult
        ?.onSuccess {
            isSheetDismissible = true
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            if (hubSheetState) {
                hubSheetState = false
                hubDetailsAction(HubDetailsAction.NavigateBack)
            }
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                    errorMessage = stringResource(error.messageId),
                    dismissAction = {
                        hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                        isSheetDismissible = true
                    }
                ))
        }

    hubDetailsState.hubDeletionResult
        ?.onSuccess {
            isSheetDismissible = true
            hubSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.NavigateBack)
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    isSheetDismissible = true
                }
            ))
        }

    hubDetailsState.itemDeletionResult
        ?.onSuccess {
            isSheetDismissible = true
            itemSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
            isItemEdit = false
        }
        ?.onError { error ->
            baseAction(
                BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                    isSheetDismissible = true
                }
            ))
        }

    SetScaffold(
        title = hubDetailsState.hub?.name ?: stringResource(com.greenvenom.core_ui.R.string.app_name),
        showLogo = false,
        navigateBackAction = { hubDetailsAction(HubDetailsAction.NavigateBack) },
        topBarActions = {
            IconButton(
                onClick = {
                    hubSheetState = true
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_ic),
                    contentDescription = stringResource(R.string.edit_hub_Details),
                    modifier = Modifier.size(48.dp)
                )
            }
        },

        floatingActionButton = {
            FloatingButton(
                isVisible = true,
                onClick = {
                    isSheetDismissible = true
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
                    itemSheetState = true
                }
            )
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filter Component
            FilterDropdownRow(
                categories = hubDetailsState.hub?.categoryList ?: emptyList(),
                manufacturers = hubDetailsState.hub?.manufacturerList ?: emptyList(),
                selectedCategory = hubDetailsState.selectedCategory ?: stringResource(R.string.all_categories),
                selectedManufacturer = hubDetailsState.selectedManufacturer ?: stringResource(R.string.all_manufacturers),
                onCategorySelected = { category ->
                    hubDetailsAction(HubDetailsAction.FilterItems(
                        category = category,
                        manufacturer = hubDetailsState.selectedManufacturer
                    ))
                },
                onManufacturerSelected = { manufacturer ->
                    hubDetailsAction(HubDetailsAction.FilterItems(
                        category = hubDetailsState.selectedCategory,
                        manufacturer = manufacturer
                    ))
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Items List
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
                                isSheetDismissible = true
                                isItemEdit = true
                                hubDetailsAction(HubDetailsAction.ChangeCurrentItem(hubItem))
                                itemSheetState = true
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .animateItem()
                        )
                    }
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_items_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (hubSheetState) {
            hubDetailsState.hub?.let { hub ->
                HubBottomSheet(
                    hub = hub.toHubUI(),
                    sheetState = sheetState,
                    onDismiss = { hubSheetState = false },
                    isEdit = true,
                    isDismissible = isSheetDismissible,
                    onEdit = { hubName, hubDescription ->
                        isSheetDismissible = false
                        hubDetailsAction(HubDetailsAction.UpdateHub(
                            hub.copy(
                                name = hubName,
                                description = hubDescription
                            )
                        ))
                    },
                    onDelete = { hubId ->
                        isSheetDismissible = false
                        hubDetailsAction(HubDetailsAction.DeleteHub(hubId))
                    }
                )
            }
        }

        if (itemSheetState) {
            ItemBottomSheet(
                sheetState = sheetState,
                isEdit = isItemEdit,
                isDismissible = isSheetDismissible,
                hubItem = hubDetailsState.currentItem?.toHubItemUI(),
                manufacturers = hubDetailsState.hub?.manufacturerList ?: emptyList(),
                categories = hubDetailsState.hub?.categoryList ?: emptyList(),
                onDismiss = {
                    itemSheetState = false
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.ChangeCurrentItem(null))
                },
                onAdd = { newItem ->
                    isSheetDismissible = false
                    hubDetailsAction(
                        HubDetailsAction.AddItem(
                            newItem
                        )
                    )
                },
                onEdit = { updatedItem ->
                    isSheetDismissible = false
                    hubDetailsAction(
                        HubDetailsAction.UpdateItem(
                            updatedItem
                        )
                    )
                },
                onDelete = { itemId ->
                    isSheetDismissible = false
                    hubDetailsAction(HubDetailsAction.DeleteItem(itemId))
                },
                onAddManufacturer = { manufacturerName ->
                    hubDetailsAction(HubDetailsAction.UpdateHub(
                        hubDetailsState.hub?.copy(
                            manufacturerList = hubDetailsState.hub.manufacturerList?.plus(
                                manufacturerName
                            )
                        ) as Hub
                    ))
                },
                onAddCategory = { categoryName ->
                    hubDetailsAction(HubDetailsAction.UpdateHub(
                        hubDetailsState.hub?.copy(
                            categoryList = hubDetailsState.hub.categoryList?.plus(
                                categoryName
                            )
                        ) as Hub
                    ))
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