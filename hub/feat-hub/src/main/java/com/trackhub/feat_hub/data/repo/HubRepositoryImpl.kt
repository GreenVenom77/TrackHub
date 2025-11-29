package com.trackhub.feat_hub.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.greenvenom.core_network.data.EmptyResult
import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

class HubRepositoryImpl(
    private val remoteDataSource: HubRemoteDataSource,
    private val cacheDataSource: HubCacheDataSource
): HubRepository {
    private val refreshHubsTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val hubSyncFlows = mutableMapOf<String, Flow<EmptyResult<NetworkError>>>()
    private val ownedHubs: MutableSet<Hub> = mutableSetOf()
    private val sharedHubs: MutableSet<Hub> = mutableSetOf()

    override fun refreshHubs() {
        refreshHubsTrigger.tryEmit(Unit)
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
        return cacheDataSource.getHub(hubId).extractHub()
    }

    override fun getHubs(isOwned: Boolean): Flow<NetworkResult<List<Hub>, NetworkError>> {
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
                    if (isOwned) {
                        ownedHubs.clear()
                        ownedHubs.addAll(cachedHubs)
                    } else {
                        sharedHubs.clear()
                        sharedHubs.addAll(cachedHubs)
                    }
                    send(NetworkResult.Success(cachedHubs))
                }
                .launchIn(this)

            refreshHubsTrigger
                .onStart { emit(Unit) }
                .onEach {
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
                            val currentHubs = if (isOwned) ownedHubs else sharedHubs
                            // Find new hubs that aren't in the current collection
                            val newHubs = fetchedHubs.filter { it !in currentHubs }

                            // Find deleted hubs that are in the collection but not in fetched data
                            val deletedHubs = currentHubs.filter { currentHub ->
                                currentHub.id !in fetchedHubs.map { it.id }
                            }

                            // Update the cache with changes
                            if (newHubs.isNotEmpty() || deletedHubs.isNotEmpty()) {
                                if (isOwned) {
                                    // Remove deleted hubs from cache
                                    if (deletedHubs.isNotEmpty()) {
                                        cacheDataSource.deleteHubs(
                                            deletedHubs.map { it.toHubEntity() }
                                        )
                                        ownedHubs.removeAll(deletedHubs.toSet())
                                    }
                                    // Add new hubs to cache
                                    if (newHubs.isNotEmpty()) {
                                        cacheDataSource.updateOwnHubs(
                                            newHubs.map { it.toHubEntity() }
                                        )
                                        ownedHubs.addAll(newHubs)
                                    }
                                } else {
                                    // Remove deleted hubs from cache
                                    if (deletedHubs.isNotEmpty()) {
                                        cacheDataSource.deleteHubs(
                                            deletedHubs.map { it.toHubEntity() }
                                        )
                                        sharedHubs.removeAll(deletedHubs.toSet())
                                    }
                                    // Add new hubs to cache
                                    if (newHubs.isNotEmpty()) {
                                        cacheDataSource.updateSharedHubs(
                                            newHubs.map { it.toHubEntity() }
                                        )
                                        sharedHubs.addAll(newHubs)
                                    }
                                }

                                // Send the updated list
                                send(NetworkResult.Success(if (isOwned) ownedHubs.toList() else sharedHubs.toList()))
                            }
                        }
                        .onError { error ->
                            send(NetworkResult.Error(error))
                        }
                }
                .launchIn(this)
        }
    }

    override suspend fun addItemToHub(
        itemInsertRequest: ItemInsertRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.addItemToHub(itemInsertRequest)
    }

    override suspend fun updateItem(
        itemUpdateRequest: ItemUpdateRequest
    ): EmptyResult<NetworkError> {
        return remoteDataSource.updateItem(itemUpdateRequest)
    }

    override suspend fun deleteHubItem(hubItemId: Int): EmptyResult<NetworkError> {
        return remoteDataSource.deleteItem(hubItemId)
    }

    private fun startHubSync(hubId: String): Flow<EmptyResult<NetworkError>> {
        return hubSyncFlows.getOrPut(hubId) {
            channelFlow {
                // Continuous remote sync
                remoteDataSource.getItemsFromHub(hubId)
                    .onEach { remoteItems ->
                        remoteItems.map { items -> items.map { it.extractItem() } }
                            .onSuccess { fetchedItems ->
                                // Compare the new items to the cached items
                                val newItems = fetchedItems.filter { item ->
                                    item !in cacheDataSource.getItemsFromHub(hubId).map { entities ->
                                        entities.extractItem()
                                    }
                                }
                                val deletedItems = cacheDataSource.getItemsFromHub(hubId).map { entities ->
                                    entities.extractItem()
                                }.filter { currentItem ->
                                    currentItem.id !in fetchedItems.map { it.id }
                                }

                                if (newItems.isNotEmpty()) {
                                    cacheDataSource.updateHubItems(
                                        newItems.map { it.toItemEntity() }
                                    )
                                }

                                if (deletedItems.isNotEmpty()) {
                                    cacheDataSource.deleteItems(
                                        deletedItems.map { it.toItemEntity() }
                                    )
                                }
                            }
                            .onError { error ->
                                send(NetworkResult.Error(error))
                            }
                    }.launchIn(this)
            }.onCompletion {
                hubSyncFlows.remove(hubId) // Clean up when sync stops
            }.shareIn(
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                started = SharingStarted.WhileSubscribed(5000), // Keep alive for 5 seconds after last subscriber
                replay = 1
            )
        }
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
        return channelFlow {
            // Start hub sync (or get existing one) - this doesn't restart on filter changes
            val syncJob = startHubSync(hubId)
                .onEach { result ->
                    if (result is NetworkResult.Error) {
                        send(result) // Report all errors
                    }
                }
                .launchIn(this)

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
                cacheDataSource.getItemsWithFiltersPaged(hubId, category, manufacturer, formattedSearchQuery)
            }.flow.map { pagingData ->
                pagingData.map { it.extractItem() }
            }

            send(NetworkResult.Success(pagedItems))
            awaitClose { syncJob.cancel() }
        }
    }
}