package com.trackhub.feat_hub.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.ErrorType
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_network.utils.getDefaultMessageId
import com.trackhub.core_hub.data.mappers.extractHub
import com.trackhub.core_hub.data.mappers.extractItem
import com.trackhub.core_hub.data.mappers.toHubEntity
import com.trackhub.core_hub.data.mappers.toItemEntity
import com.trackhub.core_hub.data.remote.dto.request.HubInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.HubUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemInsertRequest
import com.trackhub.core_hub.data.remote.dto.request.ItemUpdateRequest
import com.trackhub.core_hub.data.remote.dto.request.LeaveHubRequest
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.Item
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import com.trackhub.feat_hub.domain.remote.HubRemoteDataSource
import com.trackhub.feat_hub.domain.repo.HubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.concurrent.Volatile

class HubRepositoryImpl(
    private val remoteDataSource: HubRemoteDataSource,
    private val cacheDataSource: HubCacheDataSource
): HubRepository {
    @Volatile
    private var currentHubId: String? = null
    private val refreshHubsTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val refreshHubTrigger = MutableSharedFlow<String>(extraBufferCapacity = 1)

    override fun refreshHubs() {
        refreshHubsTrigger.tryEmit(Unit)
    }

    override fun refreshHub(hubId: String) {
        refreshHubTrigger.tryEmit(hubId)
    }

    override suspend fun addHub(
        hubInsertRequest: HubInsertRequest
    ): EmptyResult<NetworkError> {
        val remoteResult = remoteDataSource.addHub(hubInsertRequest)
        remoteResult.onSuccess { hubDto ->
            cacheDataSource.addHub(hubDto.extractHub().toHubEntity())
        }
        return remoteResult.map {  }
    }

    override suspend fun updateHub(
        hubUpdateRequest: HubUpdateRequest
    ): NetworkResult<Hub, NetworkError> {
        val remoteResult = remoteDataSource.updateHub(hubUpdateRequest)
        remoteResult.onSuccess { hubDto ->
            cacheDataSource.updateHub(hubDto.extractHub().toHubEntity())
        }
        return remoteResult.map { it.extractHub() }
    }

    override suspend fun deleteHub(hubId: String): EmptyResult<NetworkError> {
        val remoteResult = remoteDataSource.deleteHub(hubId)
        remoteResult.onSuccess { cacheDataSource.deleteHub(hubId) }
        return remoteResult
    }

    override suspend fun leaveHub(leaveHubRequest: LeaveHubRequest): EmptyResult<NetworkError> {
        val remoteResult = remoteDataSource.leaveHub(leaveHubRequest)
        remoteResult.onSuccess { cacheDataSource.deleteHub(leaveHubRequest.hubId) }
        return remoteResult
    }

    override suspend fun getHub(hubId: String): Hub {
        currentHubId = hubId
        return cacheDataSource.getHub(hubId).extractHub()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHubs(isOwned: Boolean): Flow<NetworkResult<List<Hub>, NetworkError>> {
        val foundHubs: MutableSet<Hub> = mutableSetOf()

        return channelFlow {
            val cachedHubsFlow = if (isOwned) {
                cacheDataSource.getOwnHubs().map {
                    it.map { hubEntity ->
                        hubEntity.extractHub()
                    }
                }
            } else {
                cacheDataSource.getSharedHubs().map {
                    it.map { hubEntity ->
                        hubEntity.extractHub()
                    }
                }
            }

            cachedHubsFlow
                .onEach { cachedHubs ->
                    // Update the local collection with cached data
                    foundHubs.clear()
                    foundHubs.addAll(cachedHubs)

                    send(NetworkResult.Success(cachedHubs))
                }
                .launchIn(this)

            refreshHubsTrigger
                .onStart { emit(Unit) }
                .flatMapLatest {
                    flow<Unit> {
                        // Fetch remote data and update cache
                        val remoteHubs = if (isOwned) {
                            remoteDataSource.getOwnHubs().map { hubs ->
                                hubs.map { it.extractHub() }
                            }
                        } else {
                            remoteDataSource.getSharedHubs().map { hubs ->
                                hubs.map { it.extractHub() }
                            }
                        }

                        remoteHubs
                            .onSuccess { fetchedHubs ->
                                // Find new hubs that aren't in the current collection
                                val newHubs = fetchedHubs.filter { it !in foundHubs }

                                // Find deleted hubs that are in the collection but not in fetched data
                                val deletedHubs = foundHubs.filter { currentHub ->
                                    currentHub.id !in fetchedHubs.map { it.id }
                                }

                                // Update the cache with changes
                                if (newHubs.isNotEmpty() || deletedHubs.isNotEmpty()) {
                                    // Remove deleted hubs from cache
                                    if (deletedHubs.isNotEmpty()) {
                                        cacheDataSource.deleteHubs(
                                            deletedHubs.map { it.toHubEntity() }
                                        )
                                        foundHubs.removeAll(deletedHubs.toSet())
                                    }
                                    // Add new hubs to cache
                                    if (newHubs.isNotEmpty()) {
                                        cacheDataSource.updateOwnHubs(
                                            newHubs.map { it.toHubEntity() }
                                        )
                                        foundHubs.addAll(newHubs)
                                    }

                                    // Send the updated list
                                    send(NetworkResult.Success(foundHubs.toList()))
                                } else {
                                    send(NetworkResult.Success(emptyList()))
                                }
                            }
                            .onError { error ->
                                send(NetworkResult.Error(error))
                            }
                    }
                }
                .launchIn(this)
        }
    }

    override suspend fun addItemToHub(
        itemInsertRequest: ItemInsertRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.addItemToHub(itemInsertRequest).onSuccess {
            refreshHub(itemInsertRequest.hubId)
        }
    }

    override suspend fun updateItem(
        itemUpdateRequest: ItemUpdateRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.updateItem(itemUpdateRequest).onSuccess {
            refreshHub(currentHubId ?: return@onSuccess)
        }
    }

    override suspend fun deleteHubItem(hubItemId: String): EmptyResult<NetworkError> {
        return remoteDataSource.deleteItem(hubItemId).onSuccess {
            refreshHub(currentHubId ?: return@onSuccess)
        }
    }

    /**
     * Refresh-based sync - fetches single hub and its items once and updates cache
     */
    private suspend fun syncHubAndItems(hubId: String): EmptyResult<NetworkError> {
        // Get hub from cache to determine if it's owned or shared
        val cachedHub = try {
            cacheDataSource.getHub(hubId)
        } catch (_: Exception) {
            // Hub not in cache, return error
            return NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.NOT_FOUND,
                    messageId = ErrorType.NOT_FOUND.getDefaultMessageId()
                )
            )
        }

        // Refresh the specific hub (owned or shared) and update cache
        val hubSuccess = if (cachedHub.isOwned) {
            val hubResult = remoteDataSource.getOwnHub(hubId)
            hubResult.onSuccess { hubResponse ->
                val hub = hubResponse.extractHub()
                cacheDataSource.updateHub(hub.toHubEntity())
            }
            hubResult is NetworkResult.Success
        } else {
            val hubResult = remoteDataSource.getSharedHub(hubId)
            hubResult.onSuccess { hubResponse ->
                val hub = hubResponse.extractHub()
                cacheDataSource.updateHub(hub.toHubEntity())
            }
            hubResult is NetworkResult.Success
        }

        // Fetch and sync items
        val itemsResult = remoteDataSource.getItemsFromHub(hubId)
        itemsResult.onSuccess { fetchedItemResponses ->
            val fetchedItems = fetchedItemResponses.map { it.extractItem() }

            // Get current cached items
            val cachedItems = cacheDataSource.getItemsFromHub(hubId).map { it.extractItem() }

            // Find new items that aren't in the cache
            val newItems = fetchedItems.filter { item ->
                item.id !in cachedItems.map { it.id }
            }

            // Find deleted items that are in cache but not in fetched data
            val deletedItems = cachedItems.filter { currentItem ->
                currentItem.id !in fetchedItems.map { it.id }
            }

            // Update the cache with changes
            if (newItems.isNotEmpty()) {
                cacheDataSource.updateHubItems(newItems.map { it.toItemEntity() })
            }

            if (deletedItems.isNotEmpty()) {
                cacheDataSource.deleteItems(deletedItems.map { it.toItemEntity() })
            }
        }

        // Return combined result
        return if (hubSuccess) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.UNKNOWN_ERROR,
                    messageId = ErrorType.UNKNOWN_ERROR.getDefaultMessageId()
                )
            )
        }
    }

    override suspend fun syncHub(hubId: String): EmptyResult<NetworkError> {
        return syncHubAndItems(hubId)
    }

    /**
     * Main function - lightweight and only handles filtering
     */
    override fun getItemsFromHub(
        hubId: String,
        category: String?,
        manufacturer: String?,
        searchQuery: String?
    ): Flow<NetworkResult<Flow<PagingData<Item>>, NetworkError>> {
        currentHubId = hubId

        return channelFlow {
            // Format search query for FTS
            val formattedSearchQuery = when {
                searchQuery.isNullOrEmpty() -> "**"
                searchQuery == "**" -> searchQuery
                else -> "*$searchQuery*"
            }

            // Create paging flow with current filters
            val pagedItems: Flow<PagingData<Item>> = Pager(
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 15
                )
            ) {
                cacheDataSource.getItemsWithFiltersPaged(
                    hubId,
                    category,
                    manufacturer,
                    formattedSearchQuery
                )
            }.flow.map { pagingData ->
                pagingData.map { it.extractItem() }
            }

            send(NetworkResult.Success(pagedItems))
            
            // Listen for refresh triggers and sync hub + items
            refreshHubTrigger
                .onStart { emit(hubId) }
                .onEach { triggeredHubId ->
                    if (triggeredHubId == hubId) {
                        // Sync both the hub itself and its items
                        syncHubAndItems(hubId)
                    }
                }
                .launchIn(this)
        }
    }
}