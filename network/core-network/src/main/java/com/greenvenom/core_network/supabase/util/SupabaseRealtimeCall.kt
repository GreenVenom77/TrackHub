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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

inline fun <reified T> supabaseRealtimeCall (
    crossinline execute: () -> Flow<T>
): Flow<NetworkResult<T, NetworkError>> {
    return channelFlow {
        try {
            execute().collect { data ->
                try {
                    send(NetworkResult.Success(data))
                } catch (_: NoTransformationFoundException) {
                    send(NetworkResult.Error(
                        NetworkError(
                            errorType = ErrorType.SERIALIZATION_ERROR,
                            messageId = ErrorType.SERIALIZATION_ERROR.getDefaultMessageId()
                        )
                    ))
                }
            }
        } catch (exception: RestException) {
            send(NetworkResult.Error(
                buildNetworkError(
                    statusErrorCode = exception.statusCode,
                    message = exception.description
                )
            ))
        } catch (_: HttpRequestException) {
            send(NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.NO_INTERNET,
                    messageId = ErrorType.NO_INTERNET.getDefaultMessageId()
                )
            ))
        } catch (_: HttpRequestTimeoutException) {
            send(NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.REQUEST_TIMEOUT,
                    messageId = ErrorType.REQUEST_TIMEOUT.getDefaultMessageId()
                )
            ))
        }
    }
}