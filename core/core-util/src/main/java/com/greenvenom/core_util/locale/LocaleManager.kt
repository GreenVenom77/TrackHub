package com.greenvenom.core_util.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Helper class for managing app locale using Android's built-in locale resources
 */
object LocaleManager {
    private const val DEFAULT_LANGUAGE = "en"

    /**
     * Toggles between English and Arabic.
     */
    fun toggleLanguage() {
        setLocale(if (isArabic()) "en" else "ar")
    }

    /**
     * Change app language.
     * Automatically recreates activities and updates RTL/LTR.
     */
    fun setLocale(languageTag: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    /**
     * Returns current app language tag.
     */
    fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()

        return if (!locales.isEmpty) {
            locales[0]?.toLanguageTag() ?: DEFAULT_LANGUAGE
        } else {
            Locale.getDefault().toLanguageTag()
        }
    }

    /**
     * Checks if the current language is Arabic.
     */
    fun isArabic(): Boolean {
        return getCurrentLanguage() == "ar"
    }

    /**
     * Available supported languages (only what your app supports).
     */
    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "en" to "English",
            "ar" to "العربية"
        )
    }
}
