package com.trackhub.feat_hub.presentation.hub_list

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HubListViewModel(
    private val hubRepository: HubRepository
): BaseViewModel() {
    private val _hubListState: MutableStateFlow<HubListState> = MutableStateFlow(HubListState())
    val hubListState = _hubListState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        HubListState()
    )

    private var fetchingHubsJob: Job? = null

    fun hubListAction(action: HubListAction) {
        when (action) {
            is HubListAction.AddHub -> addHub(action.hubName, action.hubDescription)
            is HubListAction.StartCollectingHubs -> {
                fetchingHubsJob = if (action.isOwned) getHubs(true) else getHubs(false)
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
                            Hub(
                                name = hubName,
                                description = hubDescription
                            )
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
            hubRepository.getHubs(isOwned = isOwned).collect { hubsResult ->
                withContext(Dispatchers.Main) {
                    hubsResult.onSuccess { hubs ->
                        _hubListState.update {
                            it.copy(
                                hubs = hubs
                            )
                        }
                    }
                    _hubListState.update {
                        it.copy(
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
        fetchingHubsJob?.cancel()
        fetchingHubsJob = null
    }
}