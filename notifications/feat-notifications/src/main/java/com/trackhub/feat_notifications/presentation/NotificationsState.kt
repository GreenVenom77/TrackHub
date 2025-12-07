package com.trackhub.feat_notifications.presentation

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_notifications.domain.models.HubInvitation
import com.trackhub.core_notifications.domain.models.InvitationAcceptance

data class NotificationsState(
    val invitations: List<HubInvitation> = emptyList(),
    val invitationsFetchingResult: EmptyResult<NetworkError>? = null,
    val invitationAcceptanceResult: NetworkResult<InvitationAcceptance, NetworkError>? = null
)
