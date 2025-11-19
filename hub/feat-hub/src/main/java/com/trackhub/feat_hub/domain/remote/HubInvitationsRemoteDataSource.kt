package com.trackhub.feat_hub.domain.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.data.remote.dto.response.HubMemberResponse
import com.trackhub.core_hub.data.remote.dto.response.InvitationResponse
import com.trackhub.core_hub.data.remote.dto.response.UserSearchResponse

interface HubInvitationsRemoteDataSource {
    suspend fun getAllHubInvitations(
        invitationsRequest: HubInvitationsRequest
    ): NetworkResult<List<HubMemberResponse>, NetworkError>

    suspend fun searchForUsers(
        searchRequest: UserSearchRequest
    ): NetworkResult<List<UserSearchResponse>, NetworkError>

    suspend fun inviteUser(
        inviteRequest: UserInviteRequest
    ): NetworkResult<InvitationResponse, NetworkError>
}