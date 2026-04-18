package com.trackhub.feat_hub.presentation.hub_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.components.buttons.FloatingButton
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseScreen
import com.greenvenom.core_ui.utils.SetScaffold
import com.trackhub.feat_hub.R
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
        ?.onSuccess {
            hubListAction(HubListAction.ClearNetworkOperations)
        }
        ?.onError { error ->
            baseAction(BaseAction.ShowErrorMessage(
                errorMessage = stringResource(error.messageId),
                dismissAction = { hubListAction(HubListAction.ClearNetworkOperations) }
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

    PullToRefreshBox(
        isRefreshing = hubListState.isRefreshing,
        onRefresh = {
            hubListAction(HubListAction.Refresh)
        },
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
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    Text(
                        text = stringResource(
                            if (areHubsOwned) R.string.no_owned_hubs_found
                            else R.string.no_shared_hubs_found
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = stringResource(
                            if (areHubsOwned) R.string.no_owned_hubs_message
                            else R.string.no_shared_hubs_message
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
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

@Preview(showBackground = true)
@Composable
private fun HubListPreview() {
    HubListContent(
        areHubsOwned = false,
        hubListState = HubListState(),
        hubListAction = {  },
        baseAction = {  },
    )
}