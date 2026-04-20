package com.trackhub.feat_notifications.domain.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_notifications.data.remote.dto.request.InvitationAcceptanceRequest
import com.trackhub.core_notifications.data.remote.dto.response.HubInvitationResponse
import com.trackhub.core_notifications.data.remote.dto.response.InvitationAcceptanceResponse

interface HubInvitationsDataSource {
    suspend fun getInvitations(): NetworkResult<List<HubInvitationResponse>, NetworkError>

    suspend fun respondToInvitation(
        acceptanceRequest: InvitationAcceptanceRequest
    ): NetworkResult<InvitationAcceptanceResponse, NetworkError>
}