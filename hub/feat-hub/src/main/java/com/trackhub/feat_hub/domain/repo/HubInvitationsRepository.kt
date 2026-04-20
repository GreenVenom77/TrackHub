package com.trackhub.feat_hub.domain.repo

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.trackhub.core_hub.domain.enums.HubRole
import com.trackhub.core_hub.domain.enums.MemberStatus
import com.trackhub.core_hub.domain.models.HubMember
import com.trackhub.core_hub.domain.models.InvitationResult
import com.trackhub.core_hub.domain.models.UserSearch

interface HubInvitationsRepository {
    suspend fun getAllHubInvitations(
        hubId: String
    ): NetworkResult<List<HubMember>, NetworkError>

    suspend fun searchForUsers(
        hubId: String,
        searchTerm: String
    ): NetworkResult<List<UserSearch>, NetworkError>

    suspend fun inviteUser(
        hubId: String,
        userId: String,
        role: HubRole
    ): NetworkResult<InvitationResult, NetworkError>

    suspend fun removeUserFromHub(
        hubId: String,
        userId: String,
        status: MemberStatus
    ): EmptyResult<NetworkError>

    suspend fun changeUserRole(
        hubId: String,
        userId: String,
        role: HubRole,
        status: MemberStatus
    ): EmptyResult<NetworkError>
}