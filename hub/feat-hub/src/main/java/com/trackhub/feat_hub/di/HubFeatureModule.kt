package com.trackhub.feat_hub.di

import com.trackhub.feat_hub.data.cache.HubRoomDataSource
import com.trackhub.feat_hub.data.remote.HubSupabaseDataSource
import com.trackhub.feat_hub.data.repo.HubRepositoryImpl
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import com.trackhub.feat_hub.domain.remote.HubRemoteDataSource
import com.trackhub.feat_hub.domain.repo.HubRepository
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsViewModel
import com.trackhub.feat_hub.presentation.hub_list.HubListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hubFeatureModule = module {
    single<HubCacheDataSource> {
        HubRoomDataSource(
            hubDao = get(),
            itemDao = get()
        )
    }

    single<HubRemoteDataSource> {
        HubSupabaseDataSource(
            supabaseClient = get()
        )
    }

    single<HubRepository> {
        HubRepositoryImpl(
            remoteDataSource = get(),
            cacheDataSource = get()
        )
    }

    viewModelOf(::HubListViewModel)
    viewModelOf(::HubDetailsViewModel)
}