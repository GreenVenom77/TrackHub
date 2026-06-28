package com.greenvenom.core_util.locale

fun String.normalizeDigits(): String {
    val result = StringBuilder()

    for (char in this) {
        result.append(
            when (char) {
                in '0'..'9' -> char
                in '٠'..'٩' -> ('0' + (char - '٠'))
                in '۰'..'۹' -> ('0' + (char - '۰')) // Persian support
                else -> char
            }
        )
    }

    return result.toString()
}
