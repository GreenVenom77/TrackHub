package com.trackhub.feat_hub.presentation.hub_details

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_hub.domain.enums.MemberStatus
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.domain.repo.HubInvitationsRepository
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

class HubDetailsViewModel(
    private val hubRepository: HubRepository,
    private val invitationsRepository: HubInvitationsRepository,
    val hubId: String
): BaseViewModel() {
    private val _hubDetailsState = MutableStateFlow(HubDetailsState())
    val hubDetailsState = _hubDetailsState.onStart {
        viewModelScope.launch {
            val fetchedHub = withContext(Dispatchers.IO) {
                hubRepository.getHub(hubId)
            }

            _hubDetailsState.update {
                it.copy(
                    hub = fetchedHub
                )
            }
        }
        itemsCollectionJob = getHubItems(
            hubId,
            null,
            null,
            null,
            null
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HubDetailsState()
    )

    private lateinit var itemsCollectionJob: Job

    fun hubDetailsAction(action: HubDetailsAction) {
        when (action) {
            is HubDetailsAction.SearchItems -> {
                if (itemsCollectionJob.isActive) itemsCollectionJob.cancel()

                itemsCollectionJob = getHubItems(
                    _hubDetailsState.value.hub?.id ?: "",
                    _hubDetailsState.value.selectedCategory,
                    _hubDetailsState.value.selectedManufacturer,
                    _hubDetailsState.value.selectedInStock,
                    action.searchQuery
                )
            }
            is HubDetailsAction.FilterItems -> {
                if (itemsCollectionJob.isActive) itemsCollectionJob.cancel()

                itemsCollectionJob = getHubItems(
                    _hubDetailsState.value.hub?.id ?: "",
                    action.category,
                    action.manufacturer,
                    action.inStock,
                    _hubDetailsState.value.currentSearchQuery
                )
            }
            is HubDetailsAction.RefreshHub -> refreshHub()
            is HubDetailsAction.UpdateHub -> updateHub(action.updatedHub)
            is HubDetailsAction.DeleteHub -> deleteHub(action.hubId)
            is HubDetailsAction.AddItem -> addItemToHub(action.newItem)
            is HubDetailsAction.UpdateItem -> updateItem(action.updatedItem)
            is HubDetailsAction.DeleteItem -> deleteItem(action.itemId)
            is HubDetailsAction.GetAllInvitations -> getAllInvitations()
            is HubDetailsAction.SearchForUsers -> searchForUsers(action.searchTerm)
            is HubDetailsAction.InviteUser -> inviteUser(action.userId, action.role)
            is HubDetailsAction.RemoveMember -> removeMemberFromHub(action.userId, action.status)
            is HubDetailsAction.ChangeMemberRole -> changeMemberRole(
                action.userId,
                action.role,
                action.status
            )
            is HubDetailsAction.LeaveHub -> leaveHub()
            is HubDetailsAction.ChangeCurrentItem -> updateCurrentItem(action.item)
            is HubDetailsAction.ClearUserSearch -> clearUserSearch()
            is HubDetailsAction.ClearNetworkOperations -> clearNetworkOperations()
            is HubDetailsAction.NavigateBack -> {  }
        }
    }

    private fun refreshHub() {
        _hubDetailsState.update { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            val hubId = _hubDetailsState.value.hub?.id ?: return@launch

            withContext(Dispatchers.IO) {
                hubRepository.syncHub(hubId)
            }

            val refreshedHub = withContext(Dispatchers.IO) {
                hubRepository.getHub(hubId)
            }

            _hubDetailsState.update {
                it.copy(
                    hub = refreshedHub,
                    isRefreshing = false
                )
            }
        }
    }

    private fun updateHub(updatedHub: Hub) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val updateHubResult = withContext(Dispatchers.IO) {
                hubRepository.updateHub(
                    id = updatedHub.id,
                    name = updatedHub.name,
                    description = updatedHub.description,
                    manufacturerList = updatedHub.manufacturerList,
                    categoryList = updatedHub.categoryList
                )
            }

            updateHubResult.onSuccess { hub ->
                _hubDetailsState.update { it.copy(hub = hub) }
            }

            _hubDetailsState.update {
                it.copy(hubUpdateResult = updateHubResult.map {  })
            }

            baseAction(BaseAction.HideLoading)
        }
    }

    private fun deleteHub(hubId: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val deleteHubResult = withContext(Dispatchers.IO) {
                hubRepository.deleteHub(hubId)
            }

            _hubDetailsState.update { it.copy(hubDeletionResult = deleteHubResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun addItemToHub(newItem: Item) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val addItemResult = withContext(Dispatchers.IO) {
                hubRepository.addItemToHub(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    name = newItem.name,
                    stockCount = newItem.stockCount,
                    unit = newItem.unit,
                    manufacturer = newItem.manufacturer,
                    category = newItem.category
                )
            }

            _hubDetailsState.update { it.copy(operationResult = addItemResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun updateItem(updatedItem: Item) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val updateItemResult = withContext(Dispatchers.IO) {
                hubRepository.updateItem(
                    id = updatedItem.id,
                    name = updatedItem.name,
                    stockCount = updatedItem.stockCount,
                    unit = updatedItem.unit,
                    imageUrl = updatedItem.imageUrl,
                    manufacturer = updatedItem.manufacturer,
                    category = updatedItem.category
                )
            }

            _hubDetailsState.update { it.copy(operationResult = updateItemResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun deleteItem(itemId: String) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val deleteItemResult = withContext(Dispatchers.IO) {
                hubRepository.deleteHubItem(itemId)
            }

            _hubDetailsState.update { it.copy(itemDeletionResult = deleteItemResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun updateCurrentItem(item: Item?) {
        _hubDetailsState.update { it.copy(currentItem = item) }
    }

    private fun getHubItems(
        hubId: String,
        category: String?,
        manufacturer: String?,
        inStock: Boolean?,
        searchQuery: String?
    ): Job {
        baseAction(BaseAction.ShowLoading)
        _hubDetailsState.update {
            it.copy(
                selectedCategory = category,
                selectedManufacturer = manufacturer,
                selectedInStock = inStock,
                currentSearchQuery = searchQuery
            )
        }

        return viewModelScope.launch(Dispatchers.IO) {
            hubRepository.getItemsFromHub(
                hubId,
                category,
                manufacturer,
                inStock,
                searchQuery
            ).collectLatest { itemsResult ->
                withContext(Dispatchers.Main) {
                    baseAction(BaseAction.HideLoading)
                    itemsResult.onSuccess { items ->
                        _hubDetailsState.update {
                            it.copy(items = items.cachedIn(viewModelScope))
                        }
                    }

                    _hubDetailsState.update {
                        it.copy(
                            hubItemsResult = itemsResult.map {  },
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    private fun getAllInvitations() {
        viewModelScope.launch {
            val getAllInvitationsResult = withContext(Dispatchers.IO) {
                invitationsRepository.getAllHubInvitations(
                    _hubDetailsState.value.hub?.id ?: ""
                )
            }

            getAllInvitationsResult.onSuccess { invitations ->
                _hubDetailsState.update { it.copy(invitationsList = invitations) }
            }
        }
    }

    private fun searchForUsers(searchTerm: String) {
        viewModelScope.launch {
            val searchForUsersResult = withContext(Dispatchers.IO) {
                invitationsRepository.searchForUsers(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    searchTerm = searchTerm
                )
            }

            searchForUsersResult.onSuccess { users ->
                _hubDetailsState.update { it.copy(usersList = users) }
            }
        }
    }

    private fun inviteUser(userId: String, role: HubRole) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val inviteUserResult = withContext(Dispatchers.IO) {
                invitationsRepository.inviteUser(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    userId = userId,
                    role = role
                )
            }

            getAllInvitations()
            clearUserSearch()

            _hubDetailsState.update {
                it.copy(invitationProcessResult = inviteUserResult)
            }

            baseAction(BaseAction.HideLoading)
        }
    }

    private fun removeMemberFromHub(userId: String, status: MemberStatus) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val removeUserResult = withContext(Dispatchers.IO) {
                invitationsRepository.removeUserFromHub(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    userId = userId,
                    status = status
                )
            }

            getAllInvitations()

            _hubDetailsState.update { it.copy(operationResult = removeUserResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun changeMemberRole(userId: String, role: HubRole, status: MemberStatus) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val changeUserRoleResult = withContext(Dispatchers.IO) {
                invitationsRepository.changeUserRole(
                    hubId = _hubDetailsState.value.hub?.id ?: "",
                    userId = userId,
                    role = role,
                    status = status
                )
            }

            getAllInvitations()

            _hubDetailsState.update { it.copy(operationResult = changeUserRoleResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun leaveHub() {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val leaveHubResult = withContext(Dispatchers.IO) {
                hubRepository.leaveHub(hubId = _hubDetailsState.value.hub?.id ?: "")
            }

            _hubDetailsState.update { it.copy(hubDeletionResult = leaveHubResult) }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun clearUserSearch() {
        _hubDetailsState.update { it.copy(usersList = emptyList()) }
    }

    private fun clearState() {
        _hubDetailsState.update { HubDetailsState() }
    }

    private fun clearNetworkOperations() {
        _hubDetailsState.update {
            it.copy(
                operationResult = null,
                hubItemsResult = null,
                hubDeletionResult = null,
                itemDeletionResult = null,
                hubUpdateResult = null,
                invitationProcessResult = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        itemsCollectionJob.cancel()
        clearState()
    }
}