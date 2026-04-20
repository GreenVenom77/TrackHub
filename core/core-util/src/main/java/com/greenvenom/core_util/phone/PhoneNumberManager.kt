package com.greenvenom.core_util.phone

import android.content.Context
import android.telephony.TelephonyManager
import io.michaelrocks.libphonenumber.android.AsYouTypeFormatter
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

class PhoneNumberManager(
    private val context: Context
) {
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(context)

    // Cache formatter per region to avoid re-creating on every keystroke
    private var asYouTypeFormatter: AsYouTypeFormatter? = null
    private var lastFormatterRegion: String? = null

    private fun getAsYouTypeFormatter(regionIso: String): AsYouTypeFormatter {
        if (lastFormatterRegion != regionIso || asYouTypeFormatter == null) {
            asYouTypeFormatter = phoneUtil.getAsYouTypeFormatter(regionIso)
            lastFormatterRegion = regionIso
        }
        return asYouTypeFormatter!!
    }

    /**
     * Resolves the SIM country ISO when no countryIso is provided.
     * Central place used by all methods.
     */
    private fun resolveRegion(countryIso: String?): String {
        if (!countryIso.isNullOrBlank()) return countryIso.uppercase()
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simCountryIso.uppercase()
    }

    /**
     * Parse raw user input and format it for backend (E164 with prefix)
     */
    fun formatForBackend(
        rawNumber: String,
        countryIso: String? = null
    ): String? {
        return safeParseAndFormat(
            rawNumber,
            resolveRegion(countryIso),
            AppPhoneFormat.BACKEND_WITH_PREFIX
        )
    }

    /**
     * Format a number coming FROM backend (+2010...)
     * Can return with or without prefix
     */
    fun formatFromBackend(
        backendNumber: String,
        countryIso: String? = null,
        format: AppPhoneFormat
    ): String? {
        return safeParseAndFormat(
            backendNumber,
            resolveRegion(countryIso),
            format
        )
    }

    /**
     * Real-time formatting as the user types — digit by digit.
     * Returns the formatted string on each keystroke (e.g. "012 345 6789").
     * Does NOT validate — returns partial results for incomplete numbers.
     *
     * @param rawInput the full input string so far (digits only, no spaces/dashes)
     * @param countryIso optional region override, falls back to SIM country
     */
    fun formatAsTyping(
        rawInput: String,
        countryIso: String? = null
    ): String {
        val region = resolveRegion(countryIso)
        val formatter = getAsYouTypeFormatter(region)
        formatter.clear()

        // Feed digits one by one — AsYouTypeFormatter is stateful
        var result = ""
        for (char in rawInput.filter { it.isDigit() }) {
            result = formatter.inputDigit(char)
        }
        return result
    }

    fun getPrefix(
        countryIso: String? = null,
        withPlusSign: Boolean = true
    ): String? {
        return try {
            val code = phoneUtil.getCountryCodeForRegion(resolveRegion(countryIso))
            if (code > 0) if (withPlusSign) "+$code" else "$code" else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Central safe formatter — validates before formatting.
     * Not suitable for real-time typing (use formatAsTyping instead).
     */
    private fun safeParseAndFormat(
        number: String,
        countryIso: String,
        format: AppPhoneFormat
    ): String? {
        return try {
            val proto = phoneUtil.parse(number, countryIso)
            if (!phoneUtil.isValidNumber(proto)) return null

            when (format) {
                AppPhoneFormat.BACKEND_WITH_PREFIX ->
                    phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.E164)

                AppPhoneFormat.USER_WITH_PREFIX ->
                    phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)

                AppPhoneFormat.USER_WITHOUT_PREFIX ->
                    phoneUtil.format(proto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            }
        } catch (e: Exception) {
            null
        }
    }
}