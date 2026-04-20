package com.trackhub.core_notifications.data.utils

import com.trackhub.core_notifications.R
import com.trackhub.core_notifications.data.remote.dto.response.InvitationAcceptanceResponse

fun parseResponse(response: InvitationAcceptanceResponse): Int {
    if (!response.success) {
        return parseErrorMessage(response.message)
    }

    return parseSuccessMessage(response.message)
}

private fun parseSuccessMessage(message: String): Int {
    return when {
        message.contains("Successfully joined hub", ignoreCase = true) -> {
            R.string.invitation_accepted_success
        }

        message.contains("Declined invitation to hub", ignoreCase = true) -> {
            R.string.invitation_declined_success
        }

        else -> R.string.invitation_response_success
    }
}

private fun parseErrorMessage(message: String): Int {
    return when {
        message.contains("not authenticated", ignoreCase = true) -> {
            R.string.error_not_authenticated
        }

        message.contains("not found", ignoreCase = true) ||
                message.contains("do not have permission", ignoreCase = true) -> {
            R.string.error_invitation_not_found
        }

        message.contains("already been responded to", ignoreCase = true) -> {
            R.string.error_invitation_already_responded
        }

        else -> R.string.error_generic
    }
}