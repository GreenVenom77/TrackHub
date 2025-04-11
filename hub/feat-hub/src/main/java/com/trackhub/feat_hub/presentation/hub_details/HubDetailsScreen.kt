package com.trackhub.feat_hub.presentation.hub_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_network.utils.toString
import com.greenvenom.core_ui.components.FloatingButton
import com.greenvenom.core_ui.components.TopAppBar
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.trackhub.feat_hub.R
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemBottomSheet
import com.trackhub.feat_hub.presentation.components.ItemListCard
import com.trackhub.feat_hub.presentation.models.toHubItemUI
import com.trackhub.feat_hub.presentation.models.toHubUI

@Composable
fun HubDetailsScreen(
    hubId: String,
    onPhysicalBack: () -> Unit,
    navigateBack: () -> Unit
) {
    BaseScreen<HubDetailsViewModel>(
        onStopAction = {
            onPhysicalBack()
        }
    ) { viewModel ->
        val hubDetailsState by viewModel.hubDetailsState.collectAsStateWithLifecycle()

        DisposableEffect(Unit) {
            viewModel.hubDetailsAction(HubDetailsAction.StartCollectingHubItems(hubId))

            onDispose {
                viewModel.hubDetailsAction(HubDetailsAction.StopCollectingHubItems)
                viewModel.hubDetailsAction(HubDetailsAction.ClearState)
            }
        }

        HubDetailsContent(
            hubDetailsState = hubDetailsState,
            hubDetailsAction = viewModel::hubDetailsAction,
            baseAction = viewModel::baseAction,
            navigateBack = navigateBack,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HubDetailsContent(
    hubDetailsState: HubDetailsState,
    hubDetailsAction: (HubDetailsAction) -> Unit,
    baseAction: (BaseAction) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hubItemsResult = hubDetailsState.hubItemsResult
    var isItemEdit by rememberSaveable { mutableStateOf(false) }
    var hubSheetState by rememberSaveable { mutableStateOf(false) }
    var itemSheetState by rememberSaveable { mutableStateOf(false) }

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
            hubDetailsAction(HubDetailsAction.UpdateCurrentItem(null))
            itemSheetState = false
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
            hubSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
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
            hubSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
            navigateBack()
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
            itemSheetState = false
            hubDetailsAction(HubDetailsAction.ClearNetworkOperations)
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

    Scaffold(
        topBar = {
            TopAppBar(
                isVisible = true,
                isSideDestination = true,
                title = hubDetailsState.hub?.name ?: stringResource(R.string.unknown_hub),
                navigateBack = { navigateBack() },
                isActionEnabled = true,
                action = {
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
                }
            )
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
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
}

@Preview(showSystemUi = true)
@Composable
private fun HubDetailsContentPreview() {
    HubDetailsContent(
        hubDetailsState = HubDetailsState(),
        hubDetailsAction = {  },
        baseAction = {  },
        navigateBack = {  }
    )
}