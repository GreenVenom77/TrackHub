package com.greenvenom.core_util.input

import com.greenvenom.core_util.input.domain.ValidationError
import com.greenvenom.core_util.input.domain.ValidationResult

object InputValidator {

    // ----------------------------
    // REGEX
    // ----------------------------
    private val emailRegex = Regex(
        """^[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)+$""",
        RegexOption.IGNORE_CASE
    )

    private val nameRegex = Regex(
        """^\p{L}(?:[\p{L}\s'’-]{0,48}\p{L})?$"""
    )

    private val specialChars = setOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '=', '[', ']', '{', '}', '|', ';', ':', ',', '.', '<', '>', '?')

    // ----------------------------
    // EMAIL
    // ----------------------------
    fun validateEmail(email: String): ValidationResult<Unit, ValidationError> {
        val trimmed = email.trim()

        return when {
            trimmed.isEmpty() ->
                ValidationResult.Error(ValidationError.EMPTY_EMAIL)

            trimmed.length > 254 ->
                ValidationResult.Error(ValidationError.EMAIL_TOO_LONG)

            !emailRegex.matches(trimmed) ->
                ValidationResult.Error(ValidationError.INVALID_EMAIL)

            else -> ValidationResult.Success(Unit)
        }
    }

    // ----------------------------
    // PASSWORD
    // ----------------------------
    fun validatePassword(
        password: String,
        isLogin: Boolean = false
    ): ValidationResult<Unit, ValidationError> {
        return when {
            password.isEmpty() ->
                ValidationResult.Error(ValidationError.EMPTY_PASSWORD)

            !isLogin && password.length > 128 ->
                ValidationResult.Error(ValidationError.PASSWORD_TOO_LONG)

            !isLogin && password.length < 8 ->
                ValidationResult.Error(ValidationError.PASSWORD_TOO_SHORT)

            !isLogin && password.none { it.isDigit() } ->
                ValidationResult.Error(ValidationError.PASSWORD_NO_DIGIT)

            !isLogin && password.none { it.isLetter() } ->
                ValidationResult.Error(ValidationError.PASSWORD_NO_LETTER)

            !isLogin && password.none { it.isUpperCase() } ->
                ValidationResult.Error(ValidationError.PASSWORD_NO_UPPER)

            !isLogin && password.none { it.isLowerCase() } ->
                ValidationResult.Error(ValidationError.PASSWORD_NO_LOWER)

            !isLogin && password.none { it in specialChars } ->
                ValidationResult.Error(ValidationError.PASSWORD_NO_SPECIAL)

            else -> ValidationResult.Success(Unit)
        }
    }

    // ----------------------------
    // NAME
    // ----------------------------
    fun validateName(name: String): ValidationResult<Unit, ValidationError> {
        val trimmed = name.trim()

        return when {
            trimmed.isEmpty() ->
                ValidationResult.Error(ValidationError.EMPTY_NAME)

            trimmed.length < 2 ->
                ValidationResult.Error(ValidationError.NAME_TOO_SHORT)

            trimmed.length > 50 ->
                ValidationResult.Error(ValidationError.NAME_TOO_LONG)

            !nameRegex.matches(trimmed) ->
                ValidationResult.Error(ValidationError.INVALID_NAME)

            else -> ValidationResult.Success(Unit)
        }
    }

    // ----------------------------
    // CONFIRM PASSWORD
    // ----------------------------
    fun validatePasswordConfirmation(
        password: String,
        confirmPassword: String
    ): ValidationResult<Unit, ValidationError> {
        return when {
            confirmPassword.isEmpty() ->
                ValidationResult.Error(ValidationError.EMPTY_PASSWORD_CONFIRMATION)

            confirmPassword != password ->
                ValidationResult.Error(ValidationError.PASSWORDS_MISMATCH)

            else -> ValidationResult.Success(Unit)
        }
    }
}