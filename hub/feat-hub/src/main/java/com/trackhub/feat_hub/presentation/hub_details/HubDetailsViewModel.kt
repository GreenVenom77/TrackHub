package com.trackhub.feat_hub.presentation.hub_details

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.core_network.data.map
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HubDetailsViewModel(
    private val hubRepository: HubRepository
): BaseViewModel() {
    private val _hubDetailsState = MutableStateFlow(HubDetailsState())
    val hubDetailsState = _hubDetailsState
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HubDetailsState()
        )

    private var itemsCollectionJob: Job? = null

    fun hubDetailsAction(action: HubDetailsAction) {
        when (action) {
            is HubDetailsAction.UpdateHub -> updateHub(
                action.hubName,
                action.hubDescription
            )
            is HubDetailsAction.DeleteHub -> deleteHub(action.hubId)
            is HubDetailsAction.AddItem -> addItemToHub(
                action.itemName,
                action.itemStock,
                action.itemUnit
            )
            is HubDetailsAction.UpdateItem -> updateItem(
                action.itemId,
                action.itemName,
                action.itemStock,
                action.itemUnit
            )
            is HubDetailsAction.DeleteItem -> deleteItem(action.itemId)
            is HubDetailsAction.UpdateCurrentItem -> updateCurrentItem(action.hubItem)
            is HubDetailsAction.StartCollectingHubItems -> {
                itemsCollectionJob = getHubItems(action.hubId)
            }
            is HubDetailsAction.StopCollectingHubItems -> {
                itemsCollectionJob?.cancel()
                itemsCollectionJob = null
            }
            is HubDetailsAction.ClearState -> clearState()
            is HubDetailsAction.ClearNetworkOperations -> clearNetworkOperations()
        }
    }

    private fun updateHub(
        hubName: String,
        hubDescription: String
    ) {
        val updatedHub = _hubDetailsState.value.hub?.copy(
            name = hubName,
            description = hubDescription
        ) as Hub

        viewModelScope.launch {
            val updateHubResult = hubRepository.updateHub(updatedHub)
            _hubDetailsState.update {
                it.copy(
                    hubUpdateResult = updateHubResult
                )
            }
        }
    }

    private fun deleteHub(hubId: String) {
        viewModelScope.launch {
            val deleteHubResult = hubRepository.deleteHub(hubId)
            _hubDetailsState.update {
                it.copy(
                    hubDeletionResult = deleteHubResult
                )
            }
        }
    }

    private fun addItemToHub(
        itemName: String,
        itemStock: Float,
        unit: String
    ) {
        viewModelScope.launch {
            val addItemResult = hubRepository.addItemToHub(
                HubItem(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    name = itemName,
                    stockCount = itemStock,
                    unit = unit
                )
            )

            _hubDetailsState.update {
                it.copy(
                    operationResult = addItemResult
                )
            }
        }
    }

    private fun updateItem(
        itemId: Int,
        itemName: String,
        itemStock: Float,
        unit: String
    ) {
        viewModelScope.launch {
            val updateItemResult = hubRepository.updateItem(
                itemId = itemId,
                itemName = itemName,
                itemStock = itemStock,
                unit = unit
            )

            _hubDetailsState.update {
                it.copy(
                    operationResult = updateItemResult
                )
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            val deleteItemResult = hubRepository.deleteHubItem(itemId)
            _hubDetailsState.update {
                it.copy(
                    itemDeletionResult = deleteItemResult
                )
            }
        }
    }

    private fun updateCurrentItem(item: HubItem?) {
        _hubDetailsState.update {
            it.copy(
                currentItem = item
            )
        }
    }

    private fun getHubItems(hubID: String): Job {
        return viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hubRepository.getHubs().first().let { hubsResult ->
                    hubsResult.map { hubs ->
                        _hubDetailsState.update {
                            it.copy(
                                hub = hubs.first { hub -> hub.id == hubID }
                            )
                        }
                    }
                }
            }

            _hubDetailsState.value.hub?.let { hub ->
                hubRepository.getItemsFromHub(hub.id).collect { itemsResult ->
                    _hubDetailsState.update {
                        it.copy(
                            hubItemsResult = itemsResult
                        )
                    }
                }
            }
        }
    }

    private fun clearState() {
        _hubDetailsState.update {
            HubDetailsState()
        }
    }

    private fun clearNetworkOperations() {
        _hubDetailsState.update {
            it.copy(
                operationResult = null,
                hubDeletionResult = null,
                itemDeletionResult = null
            )
        }
    }
}