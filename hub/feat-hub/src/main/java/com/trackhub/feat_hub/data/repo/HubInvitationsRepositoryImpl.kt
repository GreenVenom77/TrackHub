package com.trackhub.feat_hub.data.repo

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.trackhub.core_hub.data.mappers.toDomain
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.InvitationResult
import com.trackhub.core_hub.domain.models.UserSearch
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import com.trackhub.feat_hub.domain.repo.HubInvitationsRepository

class HubInvitationsRepositoryImpl(
    private val remoteDataSource: HubInvitationsRemoteDataSource
): HubInvitationsRepository {
    override suspend fun getAllHubInvitations(
        invitationsRequest: HubInvitationsRequest
    ): NetworkResult<List<HubMember>, NetworkError> {
        return remoteDataSource.getAllHubInvitations(invitationsRequest).map { invitations ->
            invitations.map { invitation -> invitation.toDomain() }
        }
    }

    override suspend fun searchForUsers(
        searchRequest: UserSearchRequest
    ): NetworkResult<List<UserSearch>, NetworkError> {
        return remoteDataSource.searchForUsers(searchRequest).map { users ->
            users.map { user -> user.toDomain() }
        }
    }

    override suspend fun inviteUser(
        inviteRequest: UserInviteRequest
    ): NetworkResult<InvitationResult, NetworkError> {
        return remoteDataSource.inviteUser(inviteRequest).map { invitation ->
            invitation.toDomain()
        }
    }
}