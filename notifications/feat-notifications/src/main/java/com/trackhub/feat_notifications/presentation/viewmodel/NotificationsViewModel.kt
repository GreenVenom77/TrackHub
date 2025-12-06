package com.trackhub.feat_notifications.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_ui.presentation.BaseAction
import com.greenvenom.core_ui.presentation.BaseViewModel
import com.trackhub.core_notifications.data.remote.dto.request.InvitationAcceptanceRequest
import com.trackhub.feat_notifications.domain.repo.NotificationsRepository
import com.trackhub.feat_notifications.presentation.NotificationsAction
import com.trackhub.feat_notifications.presentation.NotificationsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsViewModel(
    private val repository: NotificationsRepository
): BaseViewModel() {
    private val _notificationsState = MutableStateFlow(NotificationsState())
    val notificationsState = _notificationsState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(3000),
        NotificationsState()
    )

    init {
        getAllInvitations()
    }

    fun notificationsAction(action: NotificationsAction) {
        when(action) {
            is NotificationsAction.RespondToInvitation -> respondToInvitation(
                action.invitationId,
                action.accepted
            )
            NotificationsAction.ClearNetworkOperations -> clearNetworkOperations()
        }
    }

    private fun getAllInvitations() {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val getAllInvitationsResult = withContext(Dispatchers.IO) {
                repository.getInvitations()
            }

            getAllInvitationsResult.onSuccess { invitations ->
                _notificationsState.update {
                    it.copy(
                        invitations = invitations
                    )
                }
            }

            _notificationsState.update {
                it.copy(
                    invitationsFetchingResult = getAllInvitationsResult.map {  }
                )
            }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun respondToInvitation(invitationId: Int, accepted: Boolean) {
        baseAction(BaseAction.ShowLoading)
        viewModelScope.launch {
            val respondToInvitationResult = withContext(Dispatchers.IO) {
                repository.respondToInvitation(
                    InvitationAcceptanceRequest(
                        invitationId,
                        accepted
                    )
                )
            }

            val updateInvitationsDeferred = viewModelScope.async {
                getAllInvitations()
            }
            updateInvitationsDeferred.await()

            _notificationsState.update {
                it.copy(
                    invitationAcceptanceResult = respondToInvitationResult
                )
            }
            baseAction(BaseAction.HideLoading)
        }
    }

    private fun clearNetworkOperations() {
        _notificationsState.update {
            it.copy(
                invitationAcceptanceResult = null,
                invitationsFetchingResult = null
            )
        }
    }
}