package com.greenvenom.core_network.data

import androidx.annotation.StringRes
import com.greenvenom.core_network.domain.Error

data class NetworkError(
    val errorType: ErrorType,
    @param:StringRes val messageId: Int
): Error
