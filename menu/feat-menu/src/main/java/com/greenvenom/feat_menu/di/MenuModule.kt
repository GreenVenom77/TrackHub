package com.greenvenom.feat_menu.di

import com.greenvenom.feat_menu.data.cache.MenuRoomDataSource
import com.greenvenom.feat_menu.data.remote.MenuSupabaseDataSource
import com.greenvenom.feat_menu.data.repo.MenuRepositoryImpl
import com.greenvenom.feat_menu.domain.cache.MenuCacheDataSource
import com.greenvenom.feat_menu.domain.remote.MenuRemoteDataSource
import com.greenvenom.feat_menu.domain.repo.MenuRepository
import com.greenvenom.feat_menu.presentation.MenuViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val menuModule = module {
    single<MenuRemoteDataSource> {
        MenuSupabaseDataSource(
            supabaseClient = get()
        )
    }

    single<MenuCacheDataSource> {
        MenuRoomDataSource(
            profileDao = get()
        )
    }

    single<MenuRepository> {
        MenuRepositoryImpl(
            cacheDataSource = get(),
            remoteDataSource = get(),
            appPrefStateRepository = get()
        )
    }

    viewModelOf(::MenuViewModel)
}