package com.trackhub.feat_notifications.data.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.trackhub.core_notifications.data.remote.dto.request.InvitationAcceptanceRequest
import com.trackhub.core_notifications.data.remote.dto.response.HubInvitationResponse
import com.trackhub.core_notifications.data.remote.dto.response.InvitationAcceptanceResponse
import com.trackhub.feat_notifications.domain.remote.HubInvitationsDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class SupabaseHubInvitations(
    private val supabaseClient: SupabaseClient
): HubInvitationsDataSource {
    override suspend fun getInvitations(): NetworkResult<List<HubInvitationResponse>, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc("get_user_invitations").decodeList()
        }
    }

    override suspend fun respondToInvitation(
        acceptanceRequest: InvitationAcceptanceRequest
    ): NetworkResult<InvitationAcceptanceResponse, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc(
                "respond_to_invitation",
                acceptanceRequest
            ).decodeSingle()
        }
    }
}