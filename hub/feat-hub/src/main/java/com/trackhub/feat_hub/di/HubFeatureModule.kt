package com.trackhub.feat_hub.di

import com.trackhub.feat_hub.data.cache.HubRoomDataSource
import com.trackhub.feat_hub.data.remote.SupabaseHubDataSource
import com.trackhub.feat_hub.data.remote.SupabaseHubInvitationsRemoteDataSource
import com.trackhub.feat_hub.data.repo.HubInvitationsRepositoryImpl
import com.trackhub.feat_hub.data.repo.HubRepositoryImpl
import com.trackhub.feat_hub.domain.cache.HubCacheDataSource
import com.trackhub.feat_hub.domain.remote.HubInvitationsRemoteDataSource
import com.trackhub.feat_hub.domain.remote.HubRemoteDataSource
import com.trackhub.feat_hub.domain.repo.HubInvitationsRepository
import com.trackhub.feat_hub.domain.repo.HubRepository
import com.trackhub.feat_hub.presentation.hub_details.HubDetailsViewModel
import com.trackhub.feat_hub.presentation.hub_list.HubListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hubModule = module {
    single<HubCacheDataSource> {
        HubRoomDataSource(
            hubDao = get(),
            itemDao = get()
        )
    }

    single<HubRemoteDataSource> {
        SupabaseHubDataSource(
            supabaseClient = get()
        )
    }

    single<HubRepository> {
        HubRepositoryImpl(
            remoteDataSource = get(),
            cacheDataSource = get()
        )
    }

    single<HubInvitationsRemoteDataSource> {
        SupabaseHubInvitationsRemoteDataSource(
            supabaseClient = get()
        )
    }

    single<HubInvitationsRepository> {
        HubInvitationsRepositoryImpl(
            remoteDataSource = get()
        )
    }

    viewModelOf(::HubListViewModel)
    viewModelOf(::HubDetailsViewModel)
}