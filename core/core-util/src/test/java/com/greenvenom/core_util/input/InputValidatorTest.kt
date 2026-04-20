package com.greenvenom.core_util.input

import com.greenvenom.core_util.input.domain.ValidationError
import com.greenvenom.core_util.input.domain.ValidationResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class InputValidatorTest : StringSpec({

    // ----------------------------
    // EMAIL
    // ----------------------------

    "validateEmail valid" {
        InputValidator.validateEmail("test@example.com") shouldBe
                ValidationResult.Success(Unit)
    }

    "validateEmail empty" {
        InputValidator.validateEmail("") shouldBe
                ValidationResult.Error(ValidationError.EMPTY_EMAIL)
    }

    "validateEmail too long" {
        val longEmail = "a".repeat(245) + "@test.com"
        InputValidator.validateEmail(longEmail) shouldBe
                ValidationResult.Error(ValidationError.EMAIL_TOO_LONG)
    }

    "validateEmail invalid" {
        InputValidator.validateEmail("invalid") shouldBe
                ValidationResult.Error(ValidationError.INVALID_EMAIL)
    }

    // ----------------------------
    // PASSWORD
    // ----------------------------

    "validatePassword empty" {
        InputValidator.validatePassword("") shouldBe
                ValidationResult.Error(ValidationError.EMPTY_PASSWORD)
    }

    "validatePassword too short" {
        InputValidator.validatePassword("Ab1!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_TOO_SHORT)
    }

    "validatePassword too long" {
        val longPassword = "A1!".repeat(50)
        InputValidator.validatePassword(longPassword) shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_TOO_LONG)
    }

    "validatePassword missing digit" {
        InputValidator.validatePassword("Password!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_NO_DIGIT)
    }

    "validatePassword missing letter" {
        InputValidator.validatePassword("12345678!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_NO_LETTER)
    }

    "validatePassword missing special char" {
        InputValidator.validatePassword("Password123") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_NO_SPECIAL)
    }

    "validatePassword missing uppercase" {
        InputValidator.validatePassword("password123!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_NO_UPPER)
    }

    "validatePassword missing lowercase" {
        InputValidator.validatePassword("PASSWORD123!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORD_NO_LOWER)
    }

    "validatePassword valid" {
        InputValidator.validatePassword("ValidPass123!") shouldBe
                ValidationResult.Success(Unit)
    }

    // ----------------------------
    // NAME
    // ----------------------------

    "validateName empty" {
        InputValidator.validateName("") shouldBe
                ValidationResult.Error(ValidationError.EMPTY_NAME)
    }

    "validateName too short" {
        InputValidator.validateName("A") shouldBe
                ValidationResult.Error(ValidationError.NAME_TOO_SHORT)
    }

    "validateName too long" {
        val longName = "a".repeat(51)
        InputValidator.validateName(longName) shouldBe
                ValidationResult.Error(ValidationError.NAME_TOO_LONG)
    }

    "validateName invalid characters" {
        InputValidator.validateName("John123") shouldBe
                ValidationResult.Error(ValidationError.INVALID_NAME)
    }

    "validateName valid" {
        InputValidator.validateName("John Doe") shouldBe
                ValidationResult.Success(Unit)
    }

    // ----------------------------
    // PASSWORD CONFIRMATION
    // ----------------------------

    "validatePasswordConfirmation empty" {
        InputValidator.validatePasswordConfirmation("Password123!", "") shouldBe
                ValidationResult.Error(ValidationError.EMPTY_PASSWORD_CONFIRMATION)
    }

    "validatePasswordConfirmation mismatch" {
        InputValidator.validatePasswordConfirmation("Password123!", "Different123!") shouldBe
                ValidationResult.Error(ValidationError.PASSWORDS_MISMATCH)
    }

    "validatePasswordConfirmation valid" {
        InputValidator.validatePasswordConfirmation("Password123!", "Password123!") shouldBe
                ValidationResult.Success(Unit)
    }
})