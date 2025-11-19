package com.trackhub.feat_hub.data.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.data.remote.dto.response.HubMemberResponse
import com.trackhub.core_hub.data.remote.dto.response.InvitationResponse
import com.trackhub.core_hub.data.remote.dto.response.UserSearchResponse
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class SupabaseHubInvitationsRemoteDataSource(
    private val supabaseClient: SupabaseClient
): HubInvitationsRemoteDataSource {
    override suspend fun getAllHubInvitations(
        invitationsRequest: HubInvitationsRequest
    ): NetworkResult<List<HubMemberResponse>, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc(
                "get_hub_invitations",
                invitationsRequest
            ).decodeList()
        }
    }

    override suspend fun searchForUsers(
        searchRequest: UserSearchRequest
    ): NetworkResult<List<UserSearchResponse>, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc(
                "search_users_for_hub",
                searchRequest
            ).decodeList()
        }
    }

    override suspend fun inviteUser(
        inviteRequest: UserInviteRequest
    ): NetworkResult<InvitationResponse, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc(
                "invite_user_to_hub",
                inviteRequest
            ).decodeSingle()
        }
    }
}