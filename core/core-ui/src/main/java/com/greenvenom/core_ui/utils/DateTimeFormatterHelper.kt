package com.greenvenom.core_ui.utils

import androidx.appcompat.app.AppCompatDelegate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.Locale

internal fun getCurrentAppLocale(): Locale {
    return AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
}

internal fun parseDateTimeString(dateTime: String): Temporal? {
    return try {
        when {
            // Handle ISO date with time and fractional seconds
            dateTime.contains("T") && dateTime.contains(".") -> {
                val cleanedTimestamp = dateTime.substringBefore(".") + "Z"
                Instant.parse(cleanedTimestamp)
            }
            // Handle ISO date with time (with or without Z)
            dateTime.contains("T") -> {
                val cleanedTimestamp = if (!dateTime.endsWith("Z")) dateTime + "Z" else dateTime
                Instant.parse(cleanedTimestamp)
            }
            // Handle ISO date with Z suffix (date only)
            dateTime.endsWith("Z") && dateTime.length == 11 -> {
                LocalDate.parse(dateTime.dropLast(1))
            }
            // Handle simple ISO date
            dateTime.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> {
                LocalDate.parse(dateTime)
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

internal fun formatTemporal(
    temporal: Temporal,
    withTime: Boolean,
    fullMonthName: Boolean,
    locale: Locale,
    timeBelowDate: Boolean
): String {
    val monthPattern = if (fullMonthName) "MMMM" else "MMM"
    val comma = if (locale.language == "ar") "،" else ","

    val datePattern = "d $monthPattern$comma yyyy"
    val timePattern = "h:mm a"

    val pattern = when {
        withTime && timeBelowDate -> "$datePattern \n $timePattern"
        withTime -> "$datePattern  $timePattern"
        else -> datePattern
    }

    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        .withZone(ZoneId.systemDefault())

    return formatter.format(temporal)
}