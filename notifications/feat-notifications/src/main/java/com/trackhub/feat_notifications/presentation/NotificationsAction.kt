package com.trackhub.feat_notifications.presentation

interface NotificationsAction {
    data class RespondToInvitation(
        val invitationId: Int,
        val accepted: Boolean
    ) : NotificationsAction

    data object ClearNetworkOperations : NotificationsAction
}