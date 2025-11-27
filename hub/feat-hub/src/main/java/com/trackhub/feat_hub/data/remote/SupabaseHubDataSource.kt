package com.trackhub.feat_hub.data.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.greenvenom.core_network.supabase.util.supabaseRealtimeCall
import com.trackhub.core_hub.data.remote.dto.request.HubInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.LeaveHubRequest
import com.trackhub.core_hub.data.remote.dto.response.ItemResponse
import com.trackhub.core_hub.data.remote.dto.response.OwnedHubResponse
import com.trackhub.core_hub.data.remote.dto.response.SharedHubResponse
import com.trackhub.feat_hub.domain.remote.HubRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class SupabaseHubDataSource(
    private val supabaseClient: SupabaseClient
): HubRemoteDataSource {
    override suspend fun addHub(
        hubInsertRequest: HubInsertRequest
    ): NetworkResult<OwnedHubResponse, NetworkError> {
        val userId = supabaseClient.auth.currentUserOrNull()?.id as String

        return supabaseCall {
            supabaseClient.from("hubs").insert(hubInsertRequest.apply {
                addUserId(userId)
            }) {
                select()
            }.decodeSingle<OwnedHubResponse>()
        }
    }

    override suspend fun updateHub(
        hubUpdateRequest: HubUpdateRequest
    ): NetworkResult<OwnedHubResponse, NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").update(hubUpdateRequest) {
                filter { OwnedHubResponse::id eq hubUpdateRequest.id }
                select()
            }.decodeSingle<OwnedHubResponse>()
        }
    }

    override suspend fun deleteHub(hubId: String): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").delete {
                filter { OwnedHubResponse::id eq hubId }
            }
        }
    }

    override suspend fun getOwnHubs(): NetworkResult<List<OwnedHubResponse>, NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").select().decodeList<OwnedHubResponse>()
        }
    }

    override suspend fun getSharedHubs(): NetworkResult<List<SharedHubResponse>, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc("get_shared_hubs").decodeList<SharedHubResponse>()
        }
    }

    override suspend fun leaveHub(leaveHubRequest: LeaveHubRequest): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("shared_hubs").delete {
                filter { SharedHubResponse::hubId eq leaveHubRequest.hubId }
            }
        }
    }

    override suspend fun addItemToHub(
        itemInsertRequest: ItemInsertRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").insert(itemInsertRequest)
        }
    }

    override suspend fun updateItem(
        itemUpdateRequest: ItemUpdateRequest
    ): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").update(itemUpdateRequest) {
                filter { ItemResponse::id eq itemUpdateRequest.id }
            }
        }
    }

    override suspend fun deleteItem(itemId: Int): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").delete {
                filter { ItemResponse::id eq itemId }
            }
        }
    }

    override suspend fun getItemsFromHub(
        hubId: String
    ): Flow<NetworkResult<List<ItemResponse>, NetworkError>> {
        val itemsChannel = supabaseClient.realtime.channel("public:items")
        CoroutineScope(Dispatchers.IO).launch { itemsChannel.subscribe() }

        return supabaseRealtimeCall {
            itemsChannel.postgresListDataFlow(
                table = "items",
                primaryKey = ItemResponse::id,
                filter = FilterOperation(
                    column = "hub_id",
                    operator = FilterOperator.EQ,
                    value = hubId
                )
            )
        }.onCompletion { supabaseClient.realtime.removeChannel(itemsChannel) }
    }
}