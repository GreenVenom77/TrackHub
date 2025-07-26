package com.trackhub.feat_hub.presentation.hub_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.FloatingButton
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemListCard
import com.trackhub.feat_hub.presentation.models.toHubItemUI
import com.trackhub.feat_hub.presentation.models.toHubUI

@Composable
fun HubDetailsScreen(
    hubId: String,
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isItemEdit by rememberSaveable { mutableStateOf(false) }
    var hubSheetState by rememberSaveable { mutableStateOf(false) }
    var itemSheetState by rememberSaveable { mutableStateOf(false) }

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
            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
            itemSheetState = false
            isItemEdit = false
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

    hubDetailsState.hubUpdateResult
        ?.onSuccess {
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
                    }
                ))
        }

    hubDetailsState.hubDeletionResult
        ?.onSuccess {
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
                }
            ))
        }

    hubDetailsState.itemDeletionResult
        ?.onSuccess {
            itemSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
            isItemEdit = false
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

    SetScaffold(
        title = hubDetailsState.hub?.name ?: stringResource(com.greenvenom.core_ui.R.string.app_name),
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
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
                    itemSheetState = true
                },
                modifier = Modifier
                    .size(64.dp)
            )
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        hubDetailsState.hubItems.takeIf { it.isNotEmpty() }?.let { hubItems ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = hubItems,
                    key = { hubItem -> hubItem.id }
                ) { hubItem ->
                    ItemListCard(
                        hubItem = hubItem.toHubItemUI(),
                        onClick = {
                            isItemEdit = true
                            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(hubItem))
                            itemSheetState = true
                        },
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
        }

        if (hubSheetState) {
            hubDetailsState.hub?.let { hub ->
                HubBottomSheet(
                    hub = hub.toHubUI(),
                    sheetState = sheetState,
                    onDismiss = { hubSheetState = false },
                    isEdit = true,
                    onEdit = { hubName, hubDescription ->
                        hubDetailsAction(HubDetailsAction.UpdateHub(hubName, hubDescription))
                    },
                    onDelete = { hubId ->
                        hubDetailsAction(HubDetailsAction.DeleteHub(hubId))
                    }
                )
            }
        }

        if (itemSheetState) {
            ItemBottomSheet(
                sheetState = sheetState,
                isEdit = isItemEdit,
                hubItem = hubDetailsState.currentItem?.toHubItemUI(),
                onDismiss = {
                    itemSheetState = false
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
                },
                onAdd = { itemName, itemStock, unit ->
                    hubDetailsAction(
                        HubDetailsAction.AddItem(
                            itemName, itemStock.toFloat(), unit
                        )
                    )
                },
                onEdit = { itemId, itemName, itemStock, unit ->
                    hubDetailsAction(
                        HubDetailsAction.UpdateItem(
                            itemId, itemName, itemStock.toFloat(), unit
                        )
                    )
                },
                onDelete = { itemId ->
                    hubDetailsAction(HubDetailsAction.DeleteItem(itemId))
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HubDetailsContentPreview() {
    HubDetailsContent(
        hubDetailsState = HubDetailsState(),
        hubDetailsAction = {  },
        baseAction = {  }
    )
}