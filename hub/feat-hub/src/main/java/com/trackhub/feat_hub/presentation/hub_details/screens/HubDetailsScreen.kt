package com.trackhub.feat_hub.presentation.hub_details.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_network.utils.toString
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemListCard
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsAction
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsState
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsViewModel
import com.trackhub.feat_hub.presentation.models.toHubItemUI
import com.trackhub.feat_hub.presentation.models.toHubUI

@Composable
fun HubDetailsScreen(
    hubId: String,
    onPhysicalBack: () -> Unit,
    onHubDeletion: () -> Unit,
    onHubRetrieval: (Hub) -> Unit,
    hubBottomSheetState: Boolean,
    itemBottomSheetState: Boolean,
    onSheetDismiss: () -> Unit,
    onEditItem: () -> Unit,
    navigateBack: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    BaseScreen<HubDetailsViewModel> { viewModel ->
        val hubDetailsState by viewModel.hubDetailsState.collectAsStateWithLifecycle()

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        viewModel.hubDetailsAction(HubDetailsAction.StopCollectingHubItems)
                        onPhysicalBack()
                        onSheetDismiss()
                    }
                    Lifecycle.Event.ON_START -> {
                        viewModel.hubDetailsAction(HubDetailsAction.StartCollectingHubItems(hubId))
                    }
                    else -> { /* Ignore other events */ }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                viewModel.hubDetailsAction(HubDetailsAction.ClearState)
            }
        }

        HubDetailsContent(
            hubDetailsState = hubDetailsState,
            hubDetailsAction = viewModel::hubDetailsAction,
            baseAction = viewModel::baseAction,
            hubBottomSheetState = hubBottomSheetState,
            itemBottomSheetState = itemBottomSheetState,
            onSheetDismiss = onSheetDismiss,
            onHubDeletion = onHubDeletion,
            onEditItem = onEditItem,
            navigateBack = navigateBack,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        )

        hubDetailsState.hub?.let { onHubRetrieval(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HubDetailsContent(
    hubDetailsState: HubDetailsState,
    hubDetailsAction: (HubDetailsAction) -> Unit,
    baseAction: (BaseAction) -> Unit,
    hubBottomSheetState: Boolean,
    itemBottomSheetState: Boolean,
    onSheetDismiss: () -> Unit,
    onHubDeletion: () -> Unit,
    onEditItem: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hubItemsResult = hubDetailsState.hubItemsResult
    var isItemEdit by rememberSaveable { mutableStateOf(false) }

    hubItemsResult
        ?.onSuccess { baseAction(BaseAction.HideLoading) }
        ?.onError { error ->
            baseAction(BaseAction.HideLoading)
            baseAction(BaseAction.ShowErrorMessage(
                error.errorType?.toString(context) ?: stringResource(R.string.something_went_wrong)
            ))
        }

    hubDetailsState.operationResult
        ?.onSuccess {
            baseAction(BaseAction.HideLoading)
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            onSheetDismiss()
            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
            isItemEdit = false
        }
        ?.onError { error ->
            baseAction(BaseAction.HideLoading)
            baseAction(BaseAction.ShowErrorMessage(
                error.errorType?.toString(context) ?: stringResource(R.string.something_went_wrong),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                }
            ))
        }

    hubDetailsState.hubUpdateResult
        ?.onSuccess {
            baseAction(BaseAction.HideLoading)
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            onSheetDismiss()
            navigateBack()
        }
        ?.onError { error ->
            baseAction(BaseAction.HideLoading)
            baseAction(
                BaseAction.ShowErrorMessage(
                error.errorType?.toString(context) ?: stringResource(R.string.something_went_wrong),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                }
            ))
        }

    hubDetailsState.hubDeletionResult
        ?.onSuccess {
            baseAction(BaseAction.HideLoading)
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            onHubDeletion()
        }
        ?.onError { error ->
            baseAction(BaseAction.HideLoading)
            baseAction(BaseAction.ShowErrorMessage(
                error.errorType?.toString(context) ?: stringResource(R.string.something_went_wrong),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                }
            ))
        }

    hubDetailsState.itemDeletionResult
        ?.onSuccess {
            baseAction(BaseAction.HideLoading)
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            onSheetDismiss()
            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
            isItemEdit = false
        }
        ?.onError { error ->
            baseAction(BaseAction.HideLoading)
            baseAction(BaseAction.ShowErrorMessage(
                error.errorType?.toString(context) ?: stringResource(R.string.something_went_wrong),
                dismissAction = {
                    hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
                }
            ))
        }

    Box(modifier = modifier.fillMaxSize()) {
        hubItemsResult?.onSuccess { hubItems ->
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
                            onEditItem()
                        },
                        modifier = Modifier
                            .padding(4.dp)
                    )
                }
            }
        }

        if (hubBottomSheetState) {
            hubDetailsState.hub?.let { hub ->
                HubBottomSheet(
                    hub = hub.toHubUI(),
                    sheetState = sheetState,
                    onDismiss = { onSheetDismiss() },
                    isEdit = true,
                    onEdit = { hubName, hubDescription ->
                        baseAction(BaseAction.ShowLoading)
                        hubDetailsAction(HubDetailsAction.UpdateHub(hubName, hubDescription))
                    },
                    onDelete = { hubId ->
                        baseAction(BaseAction.ShowLoading)
                        hubDetailsAction(HubDetailsAction.DeleteHub(hubId))
                    }
                )
            }
        }

        if (itemBottomSheetState) {
            ItemBottomSheet(
                sheetState = sheetState,
                isEdit = isItemEdit,
                hubItem = hubDetailsState.currentItem?.toHubItemUI(),
                onDismiss = {
                    onSheetDismiss()
                    isItemEdit = false
                    hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
                },
                onAdd = { itemName, itemStock, unit ->
                    baseAction(BaseAction.ShowLoading)
                    hubDetailsAction(
                        HubDetailsAction.AddItem(
                            itemName, itemStock.toFloat(), unit
                        )
                    )
                },
                onEdit = { itemId, itemName, itemStock, unit ->
                    baseAction(BaseAction.ShowLoading)
                    hubDetailsAction(
                        HubDetailsAction.UpdateItem(
                            itemId, itemName, itemStock.toFloat(), unit
                        )
                    )
                },
                onDelete = { itemId ->
                    baseAction(BaseAction.ShowLoading)
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
        baseAction = {  },
        hubBottomSheetState = false,
        onSheetDismiss = {  },
        itemBottomSheetState = false,
        onHubDeletion = {  },
        onEditItem = {  },
        navigateBack = {  }
    )
}