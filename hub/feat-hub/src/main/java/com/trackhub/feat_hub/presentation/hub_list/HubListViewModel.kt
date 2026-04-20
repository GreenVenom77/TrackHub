package com.trackhub.feat_hub.presentation.hub_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.greenvenom.core_util.logger.Logger
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HubListViewModel(
    private val hubRepository: HubRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {
    private val _hubListState: MutableStateFlow<HubListState> = MutableStateFlow(HubListState())
    val hubListState = _hubListState.onStart {
        savedStateHandle.get<Boolean>("ownedHubs")?.let { isOwned ->
            fetchingHubsJob = getHubs(isOwned)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HubListState()
    )

    private var fetchingHubsJob: Job? = null

    init {
        Logger.d(message = "HubListViewModel created")
    }

    fun hubListAction(action: HubListAction) {
        when (action) {
            is HubListAction.AddHub -> addHub(action.hubName, action.hubDescription)
            is HubListAction.Refresh -> {
                _hubListState.update {
                    it.copy(
                        isRefreshing = true
                    )
                }
                hubRepository.refreshHubs()
            }
            is HubListAction.ClearNetworkOperations -> clearNetworkOperations()
        }
    }

    private fun addHub(hubName: String, hubDescription: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            _hubListState.update {
                it.copy(
                    addHubResult = withContext(Dispatchers.IO) {
                        hubRepository.addHub(
                            ownerId = "", // Will be set by backend
                            name = hubName,
                            description = hubDescription,
                            manufacturerList = emptyList(),
                            categoryList = emptyList()
                        )
                    }
                )
            }.also {
                baseAction(BaseAction.HideLoading)
            }
        }
    }

    private fun getHubs(isOwned: Boolean): Job {
        baseAction(BaseAction.ShowLoading)
        return viewModelScope.launch(Dispatchers.IO) {
            hubRepository.getHubs(isOwned = isOwned).collectLatest { hubsResult ->
                withContext(Dispatchers.Main) {
                    hubsResult.onSuccess { hubs ->
                        if (!hubs.isEmpty())_hubListState.update {
                            it.copy(
                                hubs = hubs
                            )
                        }
                    }

                    _hubListState.update {
                        it.copy(
                            isRefreshing = false,
                            fetchingHubsResult = hubsResult.map {},
                        )
                    }
                    baseAction(BaseAction.HideLoading)
                }
            }
        }
    }

    private fun clearNetworkOperations() {
        _hubListState.update {
            it.copy(
                addHubResult = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d(message = "HubListViewModel cleared")
        fetchingHubsJob?.cancel()
        fetchingHubsJob = null
    }
}