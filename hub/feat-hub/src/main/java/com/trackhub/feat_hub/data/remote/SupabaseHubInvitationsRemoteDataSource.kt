package com.trackhub.feat_hub.data.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.data.remote.dto.response.HubMemberResponse
import com.trackhub.core_hub.data.remote.dto.response.UserSearchResponse
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import io.github.jan.supabase.SupabaseClient

class SupabaseHubInvitationsRemoteDataSource(
    private val supabaseClient: SupabaseClient
): HubInvitationsRemoteDataSource {
    override suspend fun getAllHubInvitations(
        invitationsRequest: HubInvitationsRequest
    ): NetworkResult<List<HubMemberResponse>, NetworkError> {
        TODO("Not yet implemented")
    }

    override suspend fun searchForUsers(
        searchRequest: UserSearchRequest
    ): NetworkResult<List<UserSearchResponse>, NetworkError> {
        TODO("Not yet implemented")
    }

    override suspend fun inviteUser(
        inviteRequest: UserInviteRequest
    ): EmptyResult<NetworkError> {
        TODO("Not yet implemented")
    }
}