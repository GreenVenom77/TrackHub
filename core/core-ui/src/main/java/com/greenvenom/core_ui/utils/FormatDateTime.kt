package com.greenvenom.core_ui.utils

fun formatDateTime(
    dateTime: String,
    withTime: Boolean = true,
    fullMonthName: Boolean = false,
    timeBelowDate: Boolean = false
): String {
    val locale = getCurrentAppLocale()
    val parsedTemporal = parseDateTimeString(dateTime) ?: return " "
    return formatTemporal(parsedTemporal, withTime, fullMonthName, locale, timeBelowDate)
}