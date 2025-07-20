package com.trackhub.feat_hub.presentation.hub_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HubDetailsViewModel(
    private val hubRepository: HubRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {
    private val _hubDetailsState = MutableStateFlow(HubDetailsState())
    val hubDetailsState = _hubDetailsState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        HubDetailsState()
    )

    private var itemsCollectionJob: Job? = null

    init {
        savedStateHandle.get<String>("hubId")?.let { hubId ->
            itemsCollectionJob = getHubItems(hubId)
        }
    }

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
            is HubDetailsAction.ClearNetworkOperations -> clearNetworkOperations()
            is HubDetailsAction.NavigateBack -> {  }
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

        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val updateHubResult = hubRepository.updateHub(updatedHub)
            _hubDetailsState.update {
                it.copy(
                    hubUpdateResult = updateHubResult
                )
            }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun deleteHub(hubId: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val deleteHubResult = hubRepository.deleteHub(hubId)
            _hubDetailsState.update {
                it.copy(
                    hubDeletionResult = deleteHubResult
                )
            }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun addItemToHub(
        itemName: String,
        itemStock: Float,
        unit: String
    ) {
        baseAction(BaseAction.ShowLoading)
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
            }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun updateItem(
        itemId: Int,
        itemName: String,
        itemStock: Float,
        unit: String
    ) {
        baseAction(BaseAction.ShowLoading)
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
            }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val deleteItemResult = hubRepository.deleteHubItem(itemId)
            _hubDetailsState.update {
                it.copy(
                    itemDeletionResult = deleteItemResult
                )
            }.also {
                baseAction(BaseAction.HideLoading)
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

    private fun getHubItems(hubId: String): Job {
        baseAction(BaseAction.ShowLoading)
        return viewModelScope.launch(Dispatchers.IO) {
           val fetchedHub = hubRepository.getHub(hubId)

            withContext(Dispatchers.Main) {
                _hubDetailsState.update {
                    it.copy(
                        hub = fetchedHub
                    )
                }
            }.also {
                baseAction(BaseAction.HideLoading)
                hubRepository.getItemsFromHub(fetchedHub.id).collect { itemsResult ->
                    withContext(Dispatchers.Main) {
                        itemsResult.onSuccess { items ->
                            _hubDetailsState.update {
                                it.copy(
                                    hubItems = items
                                )
                            }
                        }

                        _hubDetailsState.update {
                            it.copy(
                                hubItemsResult = itemsResult.map {  }
                            )
                        }
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

    override fun onCleared() {
        super.onCleared()

        itemsCollectionJob?.cancel()
        itemsCollectionJob = null
        clearState()
    }
}