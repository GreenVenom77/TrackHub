package com.trackhub.feat_notifications.data.repo

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.trackhub.core_notifications.data.mappers.toDomain
import com.trackhub.core_notifications.data.remote.dto.request.InvitationAcceptanceRequest
import com.trackhub.core_notifications.domain.models.HubInvitation
import com.trackhub.core_notifications.domain.models.InvitationAcceptance
import com.trackhub.feat_notifications.domain.remote.HubInvitationsDataSource
import com.trackhub.feat_notifications.domain.repo.NotificationsRepository

class NotificationsRepositoryImpl(
    private val invitationsDataSource: HubInvitationsDataSource
): NotificationsRepository {
    override suspend fun getInvitations(): NetworkResult<List<HubInvitation>, NetworkError> {
        return invitationsDataSource.getInvitations().map { invitations ->
            invitations.map { invitation ->
                invitation.toDomain()
            }
        }
    }

    override suspend fun respondToInvitation(acceptanceRequest: InvitationAcceptanceRequest): NetworkResult<InvitationAcceptance, NetworkError> {
        return invitationsDataSource.respondToInvitation(acceptanceRequest).map { invitationAcceptance ->
            invitationAcceptance.toDomain()
        }
    }
}