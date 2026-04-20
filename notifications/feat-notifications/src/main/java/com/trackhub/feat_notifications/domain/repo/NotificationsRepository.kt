package com.trackhub.feat_notifications.domain.repo

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_notifications.domain.models.HubInvitation
import com.trackhub.core_notifications.domain.models.InvitationAcceptance

interface NotificationsRepository {
    suspend fun getInvitations(): NetworkResult<List<HubInvitation>, NetworkError>

    suspend fun respondToInvitation(
        invitationId: String,
        accepted: Boolean
    ): NetworkResult<InvitationAcceptance, NetworkError>
}