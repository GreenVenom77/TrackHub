package com.trackhub.feat_hub.presentation.hub_list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.trackhub.feat_hub.presentation.components.HubBottomSheet
import com.trackhub.feat_hub.presentation.components.HubListCard
import com.trackhub.feat_hub.presentation.mappers.toHubUI

@Composable
fun HubListScreen(
    areHubsOwned: Boolean,
    navigateToHubDetails: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    BaseScreen<HubListViewModel>(
        enableCustomBack = !areHubsOwned,
        onPhysicalBack = {
            navigateBack()
        }
    ) { viewModel ->
        val hubListState by viewModel.hubListState.collectAsStateWithLifecycle()

        LaunchedEffect(areHubsOwned) {
            viewModel.hubListAction(HubListAction.StartCollectingHubs(areHubsOwned))
        }

        HubListContent(
            areHubsOwned = areHubsOwned,
            hubListState = hubListState,
            hubListAction = { action ->
                when (action) {
                    is HubListAction.NavigateToHubDetails -> navigateToHubDetails(action.hubId)
                }
                viewModel.hubListAction(action)
            },
            baseAction = viewModel::baseAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HubListContent(
    areHubsOwned: Boolean,
    hubListState: HubListState,
    hubListAction: (HubListAction) -> Unit,
    baseAction: (BaseAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var hubSheetState by rememberSaveable { mutableStateOf(false) }
    var isSheetDismissible by rememberSaveable { mutableStateOf(true) }

    hubListState.fetchingHubsResult
        ?.onError { error ->
            baseAction(BaseAction.ShowErrorMessage(
                stringResource(error.messageId)
            ))
        }

    hubListState.addHubResult
        ?.onSuccess {
            hubListAction(HubListAction.ClearNetworkOperations)
            isSheetDismissible = true
            hubSheetState = false
        }
        ?.onError { error ->
            baseAction(BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = { hubListAction(HubListAction.ClearNetworkOperations) }
            ))
            isSheetDismissible = true
        }

    SetScaffold(
        floatingActionButton = {
            FloatingButton(
                isVisible = areHubsOwned,
                onClick = {
                    isSheetDismissible = true
                    hubSheetState = true
                }
            )
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        hubListState.hubs.takeIf { it.isNotEmpty() }?.let { hubs ->
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = hubs,
                    key = { hub -> hub.id }
                ) { hub ->
                    hub.toHubUI().let { hubUI ->
                        HubListCard(
                            hub = hubUI,
                            onClick = {
                                hubListAction(HubListAction.NavigateToHubDetails(hubUI.id))
                            },
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        if (hubSheetState) {
            HubBottomSheet(
                sheetState = sheetState,
                onDismiss = {
                    hubSheetState = false
                },
                isEdit = false,
                isDismissible = isSheetDismissible,
                onAdd = { hubName, hubDescription ->
                    isSheetDismissible = false
                    hubListAction(HubListAction.AddHub(
                        hubName, hubDescription
                    ))
                }
            )
        }
    }
}

@Preview
@Composable
private fun HubListPreview() {
    HubListContent(
        areHubsOwned = true,
        hubListState = HubListState(),
        hubListAction = {  },
        baseAction = {  },
    )
}