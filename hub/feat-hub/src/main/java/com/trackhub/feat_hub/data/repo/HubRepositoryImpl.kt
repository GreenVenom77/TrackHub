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
        ownerId: String,
        name: String,
        description: String?,
        manufacturerList: List<String>,
        categoryList: List<String>
    ): EmptyResult<NetworkError> {
        val request = HubInsertRequest(
            ownerId = ownerId,
            name = name,
            description = description,
            manufacturerList = manufacturerList,
            categoryList = categoryList
        )

        // Persist remotely first since we need the server-generated id to cache it
        val remoteResult = remoteDataSource.addHub(request)
        remoteResult.onSuccess { hubDto ->
            cacheDataSource.addHub(hubDto.extractHub().toHubEntity())
        }
        return remoteResult.map { }
    }

    override suspend fun updateHub(
        id: String,
        name: String,
        description: String?,
        manufacturerList: List<String>,
        categoryList: List<String>
    ): NetworkResult<Hub, NetworkError> {
        val request = HubUpdateRequest(
            id = id,
            name = name,
            description = description,
            manufacturerList = manufacturerList,
            categoryList = categoryList
        )

        // Build an optimistic hub from current cached data and apply user changes immediately
        val optimisticHub = cacheDataSource.getHub(id).extractHub().copy(
            name = name,
            description = description,
            manufacturerList = manufacturerList,
            categoryList = categoryList
        )
        cacheDataSource.updateHub(optimisticHub.toHubEntity())

        // Sync with remote in the background and reconcile if the response differs
        val remoteResult = remoteDataSource.updateHub(request)
        remoteResult.onSuccess { hubDto ->
            val remoteHub = hubDto.extractHub()
            if (remoteHub != optimisticHub) {
                cacheDataSource.updateHub(remoteHub.toHubEntity())
            }
        }

        return remoteResult.map { it.extractHub() }
    }

    override suspend fun deleteHub(hubId: String): EmptyResult<NetworkError> {
        // Remove from cache immediately so UI reflects the deletion without delay
        cacheDataSource.deleteHub(hubId)

        val remoteResult = remoteDataSource.deleteHub(hubId)
        remoteResult.onError {
            // Remote failed, restore hub from remote so cache stays consistent
            remoteDataSource.getOwnHub(hubId).onSuccess { hubDto ->
                cacheDataSource.addHub(hubDto.extractHub().toHubEntity())
            }
        }
        return remoteResult
    }

    override suspend fun leaveHub(hubId: String): EmptyResult<NetworkError> {
        // Remove from cache immediately
        cacheDataSource.deleteHub(hubId)

        val request = LeaveHubRequest(hubId = hubId)
        val remoteResult = remoteDataSource.leaveHub(request)
        remoteResult.onError {
            // Remote failed, restore hub from remote so cache stays consistent
            remoteDataSource.getSharedHub(hubId).onSuccess { hubDto ->
                cacheDataSource.addHub(hubDto.extractHub().toHubEntity())
            }
        }
        return remoteResult
    }

    override suspend fun getHub(hubId: String): Hub {
        currentHubId = hubId
        return cacheDataSource.getHub(hubId).extractHub()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getHubs(areOwned: Boolean): Flow<NetworkResult<List<Hub>, NetworkError>> {
        // Local map used to track current state of hubs for efficient diffing
        val foundHubs: MutableMap<String, Hub> = mutableMapOf()

        return channelFlow {
            // Select the appropriate cache source based on ownership
            val cachedHubsFlow = if (areOwned) {
                cacheDataSource.getOwnHubs().map {
                    it.map { hubEntity -> hubEntity.extractHub() }
                }
            } else {
                cacheDataSource.getSharedHubs().map {
                    it.map { hubEntity -> hubEntity.extractHub() }
                }
            }

            // Observe cache and emit immediately to show data without waiting for network
            cachedHubsFlow
                .onEach { cachedHubs ->
                    foundHubs.clear()
                    foundHubs.putAll(cachedHubs.associateBy { it.id })
                    send(NetworkResult.Success(cachedHubs))
                }
                .launchIn(this)

            // Listen for refresh triggers, starting with an initial emit to fetch on first load
            refreshHubsTrigger
                .onStart { emit(Unit) }
                .flatMapLatest {
                    flow<Unit> {
                        // Fetch from remote based on ownership type
                        val remoteHubs = if (areOwned) {
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
                                val fetchedMap = fetchedHubs.associateBy { it.id }

                                // Hubs present remotely but missing from local map
                                val newHubs = fetchedHubs.filter { it.id !in foundHubs }

                                // Hubs present locally but missing from remote response
                                val deletedHubs = foundHubs.values.filter { it.id !in fetchedMap }

                                // Hubs present in both but with different data
                                val updatedHubs = fetchedHubs.filter { fetchedHub ->
                                    val cached = foundHubs[fetchedHub.id]
                                    cached != null && cached != fetchedHub
                                }

                                val hasChanges = newHubs.isNotEmpty() ||
                                        deletedHubs.isNotEmpty() ||
                                        updatedHubs.isNotEmpty()

                                // Remove deleted hubs from cache and local map
                                if (deletedHubs.isNotEmpty()) {
                                    cacheDataSource.deleteHubs(deletedHubs.map { it.toHubEntity() })
                                    deletedHubs.forEach { foundHubs.remove(it.id) }
                                }

                                // Insert new hubs into cache and local map
                                if (newHubs.isNotEmpty()) {
                                    cacheDataSource.updateOwnHubs(newHubs.map { it.toHubEntity() })
                                    foundHubs.putAll(newHubs.associateBy { it.id })
                                }

                                // Upsert changed hubs into cache and overwrite in local map
                                if (updatedHubs.isNotEmpty()) {
                                    cacheDataSource.updateOwnHubs(updatedHubs.map { it.toHubEntity() })
                                    foundHubs.putAll(updatedHubs.associateBy { it.id })
                                }

                                // Only emit if something actually changed to avoid redundant recomposition
                                if (hasChanges) {
                                    send(NetworkResult.Success(foundHubs.values.toList()))
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
        hubId: String,
        name: String,
        stockCount: Float,
        unit: String,
        manufacturer: String?,
        category: String?
    ): EmptyResult<NetworkError> {
        val request = ItemInsertRequest(
            hubId = hubId,
            name = name,
            stockCount = stockCount,
            unit = unit,
            manufacturer = manufacturer,
            category = category
        )

        // Persist remotely first since we need the server-generated id to cache it
        return remoteDataSource.addItemToHub(request).onSuccess {
            refreshHub(hubId)
        }
    }

    override suspend fun updateItem(
        id: String,
        name: String,
        stockCount: Float,
        unit: String,
        imageUrl: String?,
        manufacturer: String?,
        category: String?
    ): EmptyResult<NetworkError> {
        val request = ItemUpdateRequest(
            id = id,
            name = name,
            stockCount = stockCount,
            unit = unit,
            imageUrl = imageUrl,
            manufacturer = manufacturer,
            category = category
        )

        // Build an optimistic item from current cached data and apply user changes immediately
        val cachedItems = cacheDataSource.getItemsFromHub(currentHubId ?: "")
        val optimisticItem = cachedItems.find { it.id == id }?.extractItem()?.copy(
            name = name,
            stockCount = stockCount,
            unit = unit,
            imageUrl = imageUrl,
            manufacturer = manufacturer,
            category = category
        )

        // Update cache immediately if we found the item
        optimisticItem?.let {
            cacheDataSource.updateHubItems(listOf(it.toItemEntity()))
        }

        // Sync with remote in background and reconcile if response differs
        val remoteResult = remoteDataSource.updateItem(request)
        remoteResult.onSuccess {
            refreshHub(currentHubId ?: return@onSuccess)
        }
        remoteResult.onError {
            // Remote failed, restore the original cached item
            optimisticItem?.let {
                val originalItem = cachedItems.find { entity -> entity.id == id }
                originalItem?.let { cacheDataSource.updateHubItems(listOf(it)) }
            }
        }

        return remoteResult
    }
    override suspend fun deleteHubItem(hubItemId: String): EmptyResult<NetworkError> {
        val hubId = currentHubId ?: ""

        // Find the item before deleting so we can restore it if remote fails
        val itemToDelete = cacheDataSource.getItemsFromHub(hubId).find { it.id == hubItemId }

        // Remove from cache immediately so UI reflects deletion without delay
        itemToDelete?.let {
            cacheDataSource.deleteItems(listOf(it))
        }

        val remoteResult = remoteDataSource.deleteItem(hubItemId)
        remoteResult.onError {
            // Remote failed, restore the item back into cache
            itemToDelete?.let {
                cacheDataSource.updateHubItems(listOf(it))
            }
        }

        return remoteResult
    }

    /**
     * Syncs a single hub and its items between remote and local cache.
     * Performs a three-way diff (new, deleted, updated) for items to minimize cache writes.
     * Returns an error if the hub is not found in cache or if the remote hub fetch fails.
     */
    private suspend fun syncHubAndItems(hubId: String): EmptyResult<NetworkError> {
        // Hub must exist in cache to determine ownership before hitting remote
        val cachedHub = try {
            cacheDataSource.getHub(hubId)
        } catch (_: Exception) {
            return NetworkResult.Error(
                NetworkError(
                    errorType = ErrorType.NOT_FOUND,
                    messageId = ErrorType.NOT_FOUND.getDefaultMessageId()
                )
            )
        }

        // Fetch the hub from the correct remote endpoint based on ownership and update cache
        val hubSuccess = if (cachedHub.isOwned) {
            val hubResult = remoteDataSource.getOwnHub(hubId)
            hubResult.onSuccess { hubResponse ->
                cacheDataSource.updateHub(hubResponse.extractHub().toHubEntity())
            }
            hubResult is NetworkResult.Success
        } else {
            val hubResult = remoteDataSource.getSharedHub(hubId)
            hubResult.onSuccess { hubResponse ->
                cacheDataSource.updateHub(hubResponse.extractHub().toHubEntity())
            }
            hubResult is NetworkResult.Success
        }

        // Fetch all items for this hub from remote
        val itemsResult = remoteDataSource.getItemsFromHub(hubId)
        itemsResult.onSuccess { fetchedItemResponses ->
            val fetchedItems = fetchedItemResponses.map { it.extractItem() }
            val cachedItems = cacheDataSource.getItemsFromHub(hubId).map { it.extractItem() }

            // Items present remotely but missing from cache
            val newItems = fetchedItems.filter { item ->
                item.id !in cachedItems.map { it.id }
            }

            // Items present in cache but missing from remote response
            val deletedItems = cachedItems.filter { currentItem ->
                currentItem.id !in fetchedItems.map { it.id }
            }

            // Items present in both but with different data
            val updatedItems = fetchedItems.filter { fetchedItem ->
                val cached = cachedItems.find { it.id == fetchedItem.id }
                cached != null && cached != fetchedItem
            }

            // Insert new items into cache
            if (newItems.isNotEmpty()) {
                cacheDataSource.updateHubItems(newItems.map { it.toItemEntity() })
            }

            // Upsert changed items into cache
            if (updatedItems.isNotEmpty()) {
                cacheDataSource.updateHubItems(updatedItems.map { it.toItemEntity() })
            }

            // Remove deleted items from cache
            if (deletedItems.isNotEmpty()) {
                cacheDataSource.deleteItems(deletedItems.map { it.toItemEntity() })
            }
        }

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
        inStock: Boolean?,
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
                    inStock,
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
