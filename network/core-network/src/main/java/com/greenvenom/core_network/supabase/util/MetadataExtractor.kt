package com.greenvenom.core_network.supabase.util

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

fun extractMetadata(
    metadata: JsonObject?,
    wantedValue: String
): String? {
    return try {
        metadata?.get(wantedValue)?.jsonPrimitive?.contentOrNull
    } catch (e: Exception) {
        null
    }
}