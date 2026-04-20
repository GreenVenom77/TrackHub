package com.trackhub.feat_hub.data.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.trackhub.core_hub.data.mappers.toDomain
import com.trackhub.core_hub.data.remote.dto.request.ChangeMemberRoleRequest
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.RemoveMemberRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_hub.domain.enums.MemberStatus
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.InvitationResult
import com.trackhub.core_hub.domain.models.UserSearch
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import com.trackhub.feat_hub.domain.repo.HubInvitationsRepository

class HubInvitationsRepositoryImpl(
    private val remoteDataSource: HubInvitationsRemoteDataSource
): HubInvitationsRepository {
    override suspend fun getAllHubInvitations(
        hubId: String
    ): NetworkResult<List<HubMember>, NetworkError> {
        val request = HubInvitationsRequest(hubId = hubId)
        return remoteDataSource.getAllHubInvitations(request).map { invitations ->
            invitations.map { invitation -> invitation.toDomain() }
        }
    }

    override suspend fun searchForUsers(
        hubId: String,
        searchTerm: String
    ): NetworkResult<List<UserSearch>, NetworkError> {
        val request = UserSearchRequest(hubId = hubId, searchTerm = searchTerm)
        return remoteDataSource.searchForUsers(request).map { users ->
            users.map { user -> user.toDomain() }
        }
    }

    override suspend fun inviteUser(
        hubId: String,
        userId: String,
        role: HubRole
    ): NetworkResult<InvitationResult, NetworkError> {
        val request = UserInviteRequest(
            hubId = hubId,
            userId = userId,
            roleName = role.name
        )
        return remoteDataSource.inviteUser(request).map { invitation ->
            invitation.toDomain()
        }
    }

    override suspend fun removeUserFromHub(
        hubId: String,
        userId: String,
        status: MemberStatus
    ): EmptyResult<NetworkError> {
        val request = RemoveMemberRequest(
            hubId = hubId,
            userId = userId,
            status = status
        )
        return remoteDataSource.removeUserFromHub(request)
    }

    override suspend fun changeUserRole(
        hubId: String,
        userId: String,
        role: HubRole,
        status: MemberStatus
    ): EmptyResult<NetworkError> {
        val request = ChangeMemberRoleRequest(
            hubId = hubId,
            userId = userId,
            hubRole = role.name,
            status = status
        )
        return remoteDataSource.changeUserRole(request)
    }
}