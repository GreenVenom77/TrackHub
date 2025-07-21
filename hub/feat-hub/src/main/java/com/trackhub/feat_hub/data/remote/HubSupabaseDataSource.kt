package com.trackhub.feat_hub.data.remote

import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.greenvenom.core_network.supabase.util.supabaseRealtimeCall
import com.trackhub.core_hub.data.remote.dto.HubDto
import com.trackhub.core_hub.data.remote.dto.HubItemDto
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class HubSupabaseDataSource(
    private val supabaseClient: SupabaseClient
): HubRemoteDataSource {
    override suspend fun addHub(hub: Hub): NetworkResult<HubDto, NetworkError> {
        val userId = supabaseClient.auth.currentUserOrNull()?.id as String
        val updatedHub = hub.copy(userId = userId)
        return supabaseCall {
            supabaseClient.from("hubs").insert(updatedHub.toHubDto()) {
                select()
            }.decodeSingle<HubDto>()
        }
    }

    override suspend fun updateHub(hub: Hub): NetworkResult<HubDto, NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").update(hub.toHubDto()) {
                filter { HubDto::id eq hub.id }
                select()
            }.decodeSingle<HubDto>()
        }
    }

    override suspend fun deleteHub(hubId: String): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").delete {
                filter { HubDto::id eq hubId }
            }
        }
    }

    override suspend fun getOwnHubs(): NetworkResult<List<HubDto>, NetworkError> {
        return supabaseCall {
            supabaseClient.from("hubs").select().decodeList<HubDto>()
        }
    }

    override suspend fun getSharedHubs(): NetworkResult<List<HubDto>, NetworkError> {
        return supabaseCall {
            supabaseClient.postgrest.rpc(function = "get_shared_hubs").decodeList<HubDto>()
        }
    }

    override suspend fun addItemToHub(hubItem: HubItem): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").insert(hubItem.toHubItemDto())
        }
    }

    override suspend fun updateItem(hubItem: HubItem): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").update(hubItem) {
                filter { HubItemDto::id eq hubItem.id }
            }
        }
    }

    override suspend fun deleteItem(itemId: Int): EmptyResult<NetworkError> {
        return supabaseCall {
            supabaseClient.from("items").delete {
                filter { HubItemDto::id eq itemId }
            }
        }
    }

    override suspend fun getItemsFromHub(
        hubId: String
    ): Flow<NetworkResult<List<HubItemDto>, NetworkError>> {
        val itemsChannel = supabaseClient.realtime.channel("public:items")
        CoroutineScope(Dispatchers.IO).launch { itemsChannel.subscribe() }

        return supabaseRealtimeCall {
            itemsChannel.postgresListDataFlow(
                table = "items",
                primaryKey = HubItemDto::id,
                filter = FilterOperation(
                    column = "hub_id",
                    operator = FilterOperator.EQ,
                    value = hubId
                )
            )
        }.onCompletion { supabaseClient.realtime.removeChannel(itemsChannel) }
    }
}