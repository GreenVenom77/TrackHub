package com.trackhub.feat_hub.presentation.hub_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemUpdateRequest
import com.trackhub.core_hub.domain.models.Item
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
                action.newItem.name,
                action.newItem.stockCount,
                action.newItem.unit,
                action.newItem.manufacturer,
                action.newItem.category
            )
            is HubDetailsAction.UpdateItem -> updateItem(
                action.updatedItem.name,
                action.updatedItem.stockCount,
                action.updatedItem.unit,
                action.updatedItem.manufacturer,
                action.updatedItem.category
            )
            is HubDetailsAction.DeleteItem -> deleteItem(action.itemId)
            is HubDetailsAction.ChangeCurrentItem -> updateCurrentItem(action.item)
            is HubDetailsAction.ClearNetworkOperations -> clearNetworkOperations()
            is HubDetailsAction.NavigateBack -> {  }
        }
    }

    private fun updateHub(
        hubName: String,
        hubDescription: String
    ) {
        viewModelScope.launch {
            val updateHubResult = withContext(Dispatchers.IO) {
                hubRepository.updateHub(
                    HubUpdateRequest(
                        id = _hubDetailsState.value.hub?.id ?: "",
                        name = hubName,
                        description = hubDescription
                    )
                )
            }

            _hubDetailsState.update {
                it.copy(
                    hubUpdateResult = updateHubResult
                )
            }
        }
    }

    private fun deleteHub(hubId: String) {
        viewModelScope.launch {
            val deleteHubResult = withContext(Dispatchers.IO) {
                hubRepository.deleteHub(hubId)
            }

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
        unit: String,
        manufacturer: String?,
        category: String?
    ) {
        viewModelScope.launch {
            val addItemResult = withContext(Dispatchers.IO) {
                hubRepository.addItemToHub(
                    ItemInsertRequest(
                        hubId = _hubDetailsState.value.hub?.id ?: "",
                        name = itemName,
                        stockCount = itemStock,
                        unit = unit,
                        manufacturer = manufacturer,
                        category = category
                    )
                )
            }

            _hubDetailsState.update {
                it.copy(
                    operationResult = addItemResult
                )
            }
        }
    }

    private fun updateItem(
        itemName: String,
        itemStock: Float,
        unit: String,
        manufacturer: String?,
        category: String?
    ) {
        viewModelScope.launch {
            val updateItemResult = withContext(Dispatchers.IO) {
                hubRepository.updateItem(
                    ItemUpdateRequest(
                        id = _hubDetailsState.value.currentItem?.id ?: 0,
                        name = itemName,
                        stockCount = itemStock,
                        unit = unit,
                        manufacturer = manufacturer,
                        category = category
                    )
                )
            }

            _hubDetailsState.update {
                it.copy(
                    operationResult = updateItemResult
                )
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            val deleteItemResult = withContext(Dispatchers.IO) {
                hubRepository.deleteHubItem(itemId)
            }

            _hubDetailsState.update {
                it.copy(
                    itemDeletionResult = deleteItemResult
                )
            }
        }
    }

    private fun updateCurrentItem(item: Item?) {
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
                                    items = items
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