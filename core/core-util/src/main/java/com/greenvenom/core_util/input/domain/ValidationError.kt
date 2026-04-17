package com.greenvenom.core_util.input.domain

import androidx.annotation.StringRes
import com.greenvenom.core_util.R

enum class ValidationError(@param:StringRes val messageId: Int) {

    // ----------------------------
    // EMAIL
    // ----------------------------
    EMPTY_EMAIL(R.string.error_email_empty),
    EMAIL_TOO_LONG(R.string.error_email_too_long),
    INVALID_EMAIL(R.string.error_email_invalid),

    // ----------------------------
    // PASSWORD
    // ----------------------------
    EMPTY_PASSWORD(R.string.error_password_empty),
    PASSWORD_TOO_SHORT(R.string.error_password_short),
    PASSWORD_TOO_LONG(R.string.error_password_long),
    PASSWORD_NO_DIGIT(R.string.error_password_digit),
    PASSWORD_NO_LETTER(R.string.error_password_letter),
    PASSWORD_NO_SPECIAL(R.string.error_password_special),
    PASSWORD_NO_UPPER(R.string.error_password_upper),
    PASSWORD_NO_LOWER(R.string.error_password_lower),

    EMPTY_PASSWORD_CONFIRMATION(R.string.error_password_empty),
    PASSWORDS_MISMATCH(R.string.error_password_mismatch),

    // ----------------------------
    // NAME
    // ----------------------------
    EMPTY_NAME(R.string.error_name_empty),
    NAME_TOO_SHORT(R.string.error_name_short),
    NAME_TOO_LONG(R.string.error_name_long),
    INVALID_NAME(R.string.error_name_invalid)
}