package com.trackhub.feat_hub.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.request.ChangeMemberRoleRequest
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.RemoveMemberRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.InvitationResult
import com.trackhub.core_hub.domain.models.UserSearch

interface HubInvitationsRepository {
    suspend fun getAllHubInvitations(
        invitationsRequest: HubInvitationsRequest
    ): NetworkResult<List<HubMember>, NetworkError>

    suspend fun searchForUsers(
        searchRequest: UserSearchRequest
    ): NetworkResult<List<UserSearch>, NetworkError>

    suspend fun inviteUser(
        inviteRequest: UserInviteRequest
    ): NetworkResult<InvitationResult, NetworkError>

    suspend fun removeUserFromHub(
        removalRequest: RemoveMemberRequest
    ): EmptyResult<NetworkError>

    suspend fun changeUserRole(
        changeRoleRequest: ChangeMemberRoleRequest
    ): EmptyResult<NetworkError>
}