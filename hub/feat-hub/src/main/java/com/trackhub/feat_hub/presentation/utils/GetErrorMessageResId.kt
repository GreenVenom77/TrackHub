package com.trackhub.feat_hub.presentation.utils

import com.greenvenom.core_util.logger.Logger
import com.trackhub.feat_hub.R

fun getErrorMessageResId(errorMessage: String): Int {
    Logger.d("NetworkError", "errorMessage: $errorMessage")
    return when {
        errorMessage.contains("Hub ID cannot be null", ignoreCase = true) ->
            R.string.error_hub_id_null

        errorMessage.contains("User ID cannot be null", ignoreCase = true) ->
            R.string.error_user_id_null

        errorMessage.contains("Role name cannot be empty", ignoreCase = true) ->
            R.string.error_role_name_empty

        errorMessage.contains("do not have permission", ignoreCase = true) ->
            R.string.error_no_permission

        errorMessage.contains("User not found", ignoreCase = true) ->
            R.string.error_user_not_found

        errorMessage.contains("Cannot invite yourself", ignoreCase = true) ->
            R.string.error_cannot_invite_self

        errorMessage.contains("Invalid role name", ignoreCase = true) ->
            R.string.error_invalid_role_name

        else -> R.string.error_generic
    }
}