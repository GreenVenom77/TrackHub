package com.trackhub.feat_hub.data.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.trackhub.core_hub.data.remote.dto.request.ChangeMemberRoleRequest
import com.trackhub.core_hub.data.remote.dto.request.HubInvitationsRequest
import com.trackhub.core_hub.data.remote.dto.request.RemoveMemberRequest
import com.trackhub.core_hub.data.remote.dto.request.UserInviteRequest
import com.trackhub.core_hub.data.remote.dto.request.UserSearchRequest
import com.trackhub.core_hub.data.remote.dto.response.HubMemberResponse
import com.trackhub.core_hub.data.remote.dto.response.InvitationResponse
import com.trackhub.core_hub.data.remote.dto.response.UserSearchResponse
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
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

    override suspend fun removeUserFromHub(
        removalRequest: RemoveMemberRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            if (removalRequest.status == MemberStatus.Member) {
                supabaseClient.from("shared_hubs").delete {
                    filter {
                        RemoveMemberRequest::hubId eq removalRequest.hubId
                        RemoveMemberRequest::userId eq removalRequest.userId
                    }
                }
            } else {
                supabaseClient.from("invitations").delete {
                    filter {
                        RemoveMemberRequest::hubId eq removalRequest.hubId
                        eq("invited_user_id", removalRequest.userId)
                    }
                }
            }
        }
    }

    override suspend fun changeUserRole(
        changeRoleRequest: ChangeMemberRoleRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            if (changeRoleRequest.status == MemberStatus.Member) {
                supabaseClient.from("shared_hubs").update(
                    { ChangeMemberRoleRequest::role setTo changeRoleRequest.role }
                ) {
                    filter {
                        ChangeMemberRoleRequest::hubId eq changeRoleRequest.hubId
                        ChangeMemberRoleRequest::userId eq changeRoleRequest.userId
                    }
                }
            } else {
                supabaseClient.from("invitations").update(
                    { set("hub_role", changeRoleRequest.role) }
                ) {
                    filter {
                        ChangeMemberRoleRequest::hubId eq changeRoleRequest.hubId
                        eq("invited_user_id", changeRoleRequest.userId)
                    }
                }
            }
        }
    }
}