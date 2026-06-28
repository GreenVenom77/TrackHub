package com.trackhub.feat_hub.presentation.enums

import androidx.annotation.StringRes
import com.trackhub.feat_hub.R

enum class InStockOptions(
    @param:StringRes val label: Int,
    val value: Boolean?,
    val key: String
) {
    IN_STOCK(
        R.string.in_stock,
        true,
        "in_stock"
    ),
    OUT_OF_STOCK(
        R.string.out_of_stock,
        false,
        "out_of_stock"
    ),
    ALL(
        R.string.all_stock,
        null,
        "all"
    );

    companion object {
        fun fromValue(value: Boolean?): InStockOptions {
            return entries.first { it.value == value }
        }
    }
}