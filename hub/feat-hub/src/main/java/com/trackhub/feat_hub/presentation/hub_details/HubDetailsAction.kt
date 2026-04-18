package com.trackhub.feat_hub.presentation.hub_details

import com.trackhub.core_hub.domain.HubRole
import com.trackhub.core_hub.domain.MemberStatus
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item

interface HubDetailsAction {
    data class SearchItems(
        val searchQuery: String?
    ): HubDetailsAction

    data class FilterItems(
        val category: String?,
        val manufacturer: String?
    ): HubDetailsAction

    data class UpdateHub(
        val updatedHub: Hub
    ): HubDetailsAction

    data class DeleteHub(val hubId: String): HubDetailsAction

    data class AddItem(
        val newItem: Item
    ): HubDetailsAction

    data class UpdateItem(
        val updatedItem: Item,
    ): HubDetailsAction

    data class DeleteItem(val itemId: String): HubDetailsAction

    data object GetAllInvitations: HubDetailsAction

    data class SearchForUsers(
        val searchTerm: String
    ): HubDetailsAction

    data class InviteUser(
        val userId: String,
        val role: HubRole
    ): HubDetailsAction

    data class RemoveMember(
        val userId: String,
        val status: MemberStatus
    ): HubDetailsAction

    data class ChangeMemberRole(
        val userId: String,
        val role: HubRole,
        val status: MemberStatus
    ): HubDetailsAction

    data object LeaveHub: HubDetailsAction

    data class ChangeCurrentItem(val item: Item?): HubDetailsAction

    data object ClearUserSearch: HubDetailsAction

    data object ClearNetworkOperations: HubDetailsAction

    data object RefreshHub: HubDetailsAction

    data object NavigateBack: HubDetailsAction
}