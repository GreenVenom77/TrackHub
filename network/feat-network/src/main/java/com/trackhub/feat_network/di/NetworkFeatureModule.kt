package com.trackhub.feat_network.di

import androidx.room.Room
import com.greenvenom.core_auth.domain.repository.AuthRepository
import com.greenvenom.core_menu.domain.MenuRepository
import com.greenvenom.core_network.domain.SessionRepository
import com.trackhub.feat_network.data.cache.RoomDataSource
import com.trackhub.feat_network.data.cache.db.TrackHubDatabase
import com.trackhub.feat_network.data.features.auth.AuthRepositoryImpl
import com.trackhub.feat_network.data.features.hub.HubRepositoryImpl
import com.trackhub.feat_network.data.remote.SupabaseDataSource
import com.trackhub.feat_network.domain.cache.CacheDataSource
import com.trackhub.feat_network.domain.remote.RemoteDataSource
import com.trackhub.core_hub.domain.repository.HubRepository
import com.trackhub.feat_network.data.features.menu.MenuRepositoryImpl
import com.trackhub.feat_network.data.remote.repository.SupabaseSessionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkFeatureModule = module {
    single<SessionRepository>(createdAtStart = true) {
        SupabaseSessionRepository(supabaseClient = get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(remoteDataSource = get())
    }

    single<MenuRepository> {
        MenuRepositoryImpl(
            remoteDataSource = get(),
            appPrefStateRepository = get()
        )
    }

    single<HubRepository> {
        HubRepositoryImpl(
            remoteDataSource = get(),
            cacheDataSource = get()
        )
    }

    single<RemoteDataSource>() {
        SupabaseDataSource(
            supabaseClient = get()
        )
    }

    single<CacheDataSource>() {
        RoomDataSource(hubDao = get())
    }

    single {
        get<TrackHubDatabase>().hubDao
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            TrackHubDatabase::class.java,
            name = "trackhub.db"
        ).build()
    }
}