package com.greenvenom.core_network.utils

import com.greenvenom.core_network.R
import com.greenvenom.core_network.data.ErrorType
import com.greenvenom.core_network.data.NetworkError

fun buildNetworkError(
    statusErrorCode: Int,
    message: String?
): NetworkError {
    val errorType = when (statusErrorCode) {
        400 -> ErrorType.BAD_REQUEST
        401 -> ErrorType.UNAUTHORIZED
        403 -> ErrorType.FORBIDDEN
        404 -> ErrorType.NOT_FOUND
        408 -> ErrorType.REQUEST_TIMEOUT
        409 -> ErrorType.CONFLICT
        422 -> ErrorType.SERIALIZATION_ERROR
        429 -> ErrorType.TOO_MANY_REQUESTS
        500 -> ErrorType.SERVER_ERROR
        503 -> ErrorType.SERVICE_UNAVAILABLE
        else -> ErrorType.UNKNOWN_ERROR
    }

    val customMessageStatusCodes = listOf(400, 401, 403, 409, 422)

    val shouldCheckCustomMessage = statusErrorCode in customMessageStatusCodes &&
            message != null &&
            message.isNotBlank() &&
            message.trim().isNotEmpty()

    val messageId = if (shouldCheckCustomMessage) {
        getCustomMessageId(errorType, message.trim())
    } else {
        errorType.getDefaultMessageId()
    }

    return NetworkError(
        errorType = errorType,
        messageId = messageId
    )
}

private fun getCustomMessageId(errorType: ErrorType, message: String): Int {
    val lowerMessage = message.lowercase()

    return when (errorType) {
        ErrorType.BAD_REQUEST -> {
            when {
                lowerMessage.contains("email") && lowerMessage.contains("format") -> R.string.error_invalid_email_format
                lowerMessage.contains("password") && (lowerMessage.contains("short") || lowerMessage.contains("8")) -> R.string.error_password_too_short
                else -> R.string.error_bad_request
            }
        }
        ErrorType.UNAUTHORIZED -> {
            when {
                lowerMessage.contains("username") && lowerMessage.contains("password") -> R.string.error_invalid_credentials
                lowerMessage.contains("invalid") && (lowerMessage.contains("credentials") || lowerMessage.contains("login")) -> R.string.error_invalid_credentials
                lowerMessage.contains("account") && lowerMessage.contains("locked") -> R.string.error_account_locked
                lowerMessage.contains("email") && lowerMessage.contains("verified") -> R.string.error_email_not_verified
                else -> R.string.error_unauthorized
            }
        }
        ErrorType.FORBIDDEN -> {
            when {
                lowerMessage.contains("account") && lowerMessage.contains("suspended") -> R.string.error_account_suspended
                else -> R.string.error_forbidden
            }
        }
        ErrorType.CONFLICT -> {
            when {
                lowerMessage.contains("email") && (lowerMessage.contains("taken") || lowerMessage.contains("registered") || lowerMessage.contains("exists")) -> R.string.error_email_taken
                lowerMessage.contains("username") && (lowerMessage.contains("taken") || lowerMessage.contains("exists")) -> R.string.error_username_taken
                else -> R.string.error_conflict
            }
        }
        ErrorType.SERIALIZATION_ERROR -> {
            when {
                lowerMessage.contains("email") && lowerMessage.contains("format") -> R.string.error_invalid_email_format
                lowerMessage.contains("password") && (lowerMessage.contains("short") || lowerMessage.contains("8")) -> R.string.error_password_too_short
                else -> R.string.error_serialization
            }
        }
        else -> errorType.getDefaultMessageId()
    }
}