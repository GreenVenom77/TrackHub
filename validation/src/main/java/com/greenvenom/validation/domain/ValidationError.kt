package com.greenvenom.validation.domain

import androidx.annotation.StringRes
import com.greenvenom.validation.R

enum class ValidationError(@param:StringRes val messageId: Int) {
    EMPTY_USER_NAME(R.string.empty_user_name_error),
    EMPTY_EMAIL(R.string.empty_email_error),
    EMPTY_PASSWORD(R.string.empty_password_error),
    EMPTY_PASSWORD_CONFIRMATION(R.string.empty_confirm_password_error),
    PASSWORDS_MISMATCH(R.string.password_mismatch_error),
    INVALID_EMAIL(R.string.invalid_email_error),
    INVALID_OTP(R.string.invalid_otp_error),
    MINIMUM_6_CHARACTERS(R.string.minimum_6_characters_error),
    MINIMUM_8_CHARACTERS(R.string.minimum_8_characters_error),
    MINIMUM_1_NUMBER(R.string.at_least_one_number_error),
    MINIMUM_1_LOWERCASE_LETTER(R.string.at_least_one_lowercase_letter_error),
    MINIMUM_1_UPPERCASE_LETTER(R.string.at_least_one_uppercase_letter_error),
    MINIMUM_1_SPECIAL_CHARACTER(R.string.at_least_one_special_character_error),
    EMPTY_NAME(R.string.name_cannot_be_empty),
    INVALID_CHARACTERS(R.string.name_contains_invalid_characters),
    INSUFFICIENT_PARTS(R.string.name_contains_insufficient_parts),
    INVALID_PHONE_NUMBER(R.string.invalid_phone_number)
}