package com.trackhub.feat_hub.data.repo

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.data.map
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.trackhub.core_hub.domain.models.Hub
import com.trackhub.core_hub.domain.models.HubItem
import com.trackhub.feat_hub.domain.repo.HubRepository
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import com.trackhub.feat_hub.domain.remote.HubRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class HubRepositoryImpl(
    private val remoteDataSource: HubRemoteDataSource,
    private val cacheDataSource: HubCacheDataSource
): HubRepository {
    private val refreshHubsTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val ownedHubs: MutableSet<Hub> = mutableSetOf()
    private val sharedHubs: MutableSet<Hub> = mutableSetOf()
    private val currentItems: MutableSet<HubItem> = mutableSetOf()

    override fun refreshHubs() {
        refreshHubsTrigger.tryEmit(Unit)
    }

    override suspend fun addHub(hub: Hub): NetworkResult<Unit, NetworkError> {
        val remoteResult = remoteDataSource.addHub(hub)
        remoteResult.onSuccess { returnedHub -> cacheDataSource.addHub(returnedHub) }
        return remoteResult.map {  }
    }

    override suspend fun updateHub(hub: Hub): NetworkResult<Unit, NetworkError> {
        val remoteResult = remoteDataSource.updateHub(hub)
        remoteResult.onSuccess { returnedHub -> cacheDataSource.updateHub(returnedHub) }
        return remoteResult.map {  }
    }

    override suspend fun deleteHub(hubId: String): NetworkResult<Unit, NetworkError> {
        val remoteResult = remoteDataSource.deleteHub(hubId)
        remoteResult.onSuccess { cacheDataSource.deleteHub(hubId) }
        return remoteResult
    }

    override fun getHubs(isOwned: Boolean): Flow<NetworkResult<List<Hub>, NetworkError>> {
        return channelFlow {
            val cachedHubsFlow = if (isOwned) {
                cacheDataSource.getOwnHubs()
            } else {
                cacheDataSource.getSharedHubs()
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
                        remoteDataSource.getOwnHubs()
                    } else {
                        remoteDataSource.getSharedHubs()
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
                                        cacheDataSource.deleteHubs(deletedHubs)
                                        ownedHubs.removeAll(deletedHubs.toSet())
                                    }
                                    // Add new hubs to cache
                                    if (newHubs.isNotEmpty()) {
                                        cacheDataSource.updateOwnHubs(newHubs)
                                        ownedHubs.addAll(newHubs)
                                    }
                                } else {
                                    // Remove deleted hubs from cache
                                    if (deletedHubs.isNotEmpty()) {
                                        cacheDataSource.deleteHubs(deletedHubs)
                                        sharedHubs.removeAll(deletedHubs.toSet())
                                    }
                                    // Add new hubs to cache
                                    if (newHubs.isNotEmpty()) {
                                        cacheDataSource.updateSharedHubs(newHubs)
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

    override suspend fun addItemToHub(hubItem: HubItem): NetworkResult<Unit, NetworkError> {
        return remoteDataSource.addItemToHub(hubItem)
    }

    override suspend fun updateItem(
        itemId: Int,
        itemName: String,
        itemStock: Float,
        unit: String
    ): NetworkResult<Unit, NetworkError> {
        val updatedItem = currentItems.find { it.id == itemId }
            ?.copy(name = itemName, stockCount = itemStock, unit = unit) as HubItem

        return remoteDataSource.updateItem(updatedItem)
    }

    override suspend fun deleteHubItem(hubItemId: Int): NetworkResult<Unit, NetworkError> {
        return remoteDataSource.deleteItem(hubItemId)
    }

    override fun getItemsFromHub(hubId: String): Flow<NetworkResult<List<HubItem>, NetworkError>> {
        return channelFlow {
            val cachedItemsFlow = cacheDataSource.getItemsFromHub(hubId)

            // First emit from cache immediately
            cachedItemsFlow
                .onEach { cachedItems ->
                    currentItems.removeIf { currentItem ->
                        currentItem.id in cachedItems.map { it.id }
                    }
                    currentItems.addAll(cachedItems)
                    send(NetworkResult.Success(cachedItems))
                }
                .launchIn(this)

            // Start fetching remote data
            remoteDataSource.getItemsFromHub(hubId)
                .onEach { remoteItems ->
                    remoteItems
                        .onSuccess { fetchedItems ->
                            // Compare the new items to the cached items
                            val newItems = fetchedItems.filter { item -> item !in currentItems }
                            val deletedItems = currentItems.filter { currentItem ->
                                currentItem.id !in fetchedItems.map { it.id }
                            }

                            if (newItems.isNotEmpty()) {
                                cacheDataSource.updateHubItems(newItems)
                            }

                            if (deletedItems.isNotEmpty()) {
                                cacheDataSource.deleteItems(deletedItems)
                                currentItems.removeAll(deletedItems.toSet())
                            }
                        }
                        .onError { error ->
                            send(NetworkResult.Error(error))
                        }
                }
                .launchIn(this)
        }.onCompletion { currentItems.clear() }
    }
}