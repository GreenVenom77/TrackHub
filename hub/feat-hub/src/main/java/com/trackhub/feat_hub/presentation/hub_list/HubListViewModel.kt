package com.trackhub.feat_hub.presentation.hub_list

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HubListViewModel(
    private val hubRepository: HubRepository
): BaseViewModel() {
    private val _hubListState: MutableStateFlow<HubListState> = MutableStateFlow(HubListState())
    val hubListState = _hubListState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        HubListState()
    )

    private var ownedHubsJob: Job? = null
    private var sharedHubsJob: Job?  = null

    fun hubListAction(action: HubListAction) {
        when (action) {
            is HubListAction.AddHub -> addHub(action.hubName, action.hubDescription)
            is HubListAction.StartCollectingHubs -> {
                if (action.isOwned) ownedHubsJob = getHubs(true) else sharedHubsJob = getHubs(false)
            }
            is HubListAction.StopCollectingHubs -> {
                if (action.isOwned){
                    ownedHubsJob?.cancel()
                    ownedHubsJob = null
                } else{
                    sharedHubsJob?.cancel()
                    sharedHubsJob = null
                }
            }
            is HubListAction.ClearNetworkOperations -> clearNetworkOperations()
        }
    }

    private fun addHub(hubName: String, hubDescription: String) {
        viewModelScope.launch {
            _hubListState.update {
                it.copy(
                    addHubResult = hubRepository.addHub(
                        Hub(
                            name = hubName,
                            description = hubDescription
                        )
                    )
                )
            }
        }
    }

    private fun getHubs(isOwned: Boolean): Job {
        return viewModelScope.launch {
            hubRepository.getHubs(isOwned = isOwned).collect { hubsResult ->
                _hubListState.update {
                    if (isOwned) {
                        it.copy(ownedHubsResult = hubsResult)
                    } else {
                        it.copy(sharedHubsResult = hubsResult)
                    }
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
}