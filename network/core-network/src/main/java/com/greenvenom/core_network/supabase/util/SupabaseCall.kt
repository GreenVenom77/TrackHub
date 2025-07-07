package com.greenvenom.core_network.supabase.util

import com.greenvenom.core_network.data.ErrorType
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.utils.buildNetworkError
import com.greenvenom.core_network.utils.getDefaultMessageId
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.HttpRequestTimeoutException

inline fun <reified T> supabaseCall (
    execute: () -> T
): NetworkResult<T, NetworkError> {
    val result = try {
        val executionResult = execute()
        try {
            NetworkResult.Success(executionResult)
        } catch (_: NoTransformationFoundException) {
            NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.SERIALIZATION_ERROR,
                    messageId = ErrorType.SERIALIZATION_ERROR.getDefaultMessageId()
                )
            )
        }
    } catch (exception: RestException) {
        NetworkResult.Error(
            buildNetworkError(
                statusErrorCode = exception.statusCode,
                message = exception.description
            )
        )
    } catch (_: HttpRequestException) {
        NetworkResult.Error(
            NetworkError(
                errorType = ErrorType.NO_INTERNET,
                messageId = ErrorType.NO_INTERNET.getDefaultMessageId()
            )
        )
    } catch (_: HttpRequestTimeoutException) {
        NetworkResult.Error(
            NetworkError(
                errorType = ErrorType.REQUEST_TIMEOUT,
                messageId = ErrorType.REQUEST_TIMEOUT.getDefaultMessageId()
            )
        )
    }

    return result
}