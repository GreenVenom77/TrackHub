package com.trackhub.core_hub.domain.enums

import androidx.annotation.StringRes
import com.trackhub.core_hub.R

enum class BaseUnit(
    val apiKey: String,
    @param:StringRes val displayNameRes: Int
) {
    PIECES("pieces", R.string.unit_pieces),
    KILOGRAMS("kilograms", R.string.unit_kilograms),
    GRAMS("grams", R.string.unit_grams),
    LITERS("liters", R.string.unit_liters),
    MILLILITERS("milliliters", R.string.unit_milliliters),
    METERS("meters", R.string.unit_meters),
    CENTIMETERS("centimeters", R.string.unit_centimeters),
    BOXES("boxes", R.string.unit_boxes),
    PACKS("packs", R.string.unit_packs),
    BOTTLES("bottles", R.string.unit_bottles),
    CANS("cans", R.string.unit_cans),
    BAGS("bags", R.string.unit_bags),
    ROLLS("rolls", R.string.unit_rolls),
    SETS("sets", R.string.unit_sets),
    PAIRS("pairs", R.string.unit_pairs);

    companion object {
        fun fromApiKey(apiKey: String): BaseUnit {
            return entries.find { it.apiKey == apiKey } ?: PIECES
        }
    }
}