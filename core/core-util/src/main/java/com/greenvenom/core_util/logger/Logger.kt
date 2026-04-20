package com.greenvenom.core_util.logger

import android.util.Log
import com.greenvenom.core_util.BuildConfig

object Logger {
    private const val TAG_PREFIX = "Greeve"
    private const val DEFAULT_TAG = "App"

    fun d( tag: String = DEFAULT_TAG, message: String) {
        log(LogType.DEBUG, tag, message)
    }

    fun e( tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogType.ERROR, tag, message, throwable)
    }

    fun i( tag: String = DEFAULT_TAG, message: String) {
        log(LogType.INFO, tag, message)
    }

    fun w( tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        log(LogType.WARN, tag, message, throwable)
    }

    fun v(tag: String = DEFAULT_TAG, message: String) {
        log(LogType.VERBOSE, tag, message)
    }

    private fun log(type: LogType, tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            val formattedTag = "$TAG_PREFIX | $tag"
            val formattedMessage = "${type.emoji} $message"

            try {
                when (type) {
                    LogType.DEBUG -> Log.d(formattedTag, formattedMessage)
                    LogType.ERROR -> Log.e(formattedTag, formattedMessage, throwable)
                    LogType.INFO -> Log.i(formattedTag, formattedMessage)
                    LogType.WARN -> Log.w(formattedTag, formattedMessage, throwable)
                    LogType.VERBOSE -> Log.v(formattedTag, formattedMessage)
                }
            } catch (e: RuntimeException) {
                // Fallback for non-Android environments (tests)
                println("${type.emoji} [$formattedTag]: $message")
                throwable?.printStackTrace()
            }
        }
    }

    private enum class LogType(val emoji: String) {
        DEBUG("🐛"),
        INFO("ℹ️"),
        WARN("⚠️"),
        ERROR("❌"),
        VERBOSE("🔬")
    }
}