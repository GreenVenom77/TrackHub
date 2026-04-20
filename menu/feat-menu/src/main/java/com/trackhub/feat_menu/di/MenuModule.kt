package com.trackhub.feat_menu.di

import com.trackhub.feat_menu.data.cache.MenuRoomDataSource
import com.trackhub.feat_menu.data.remote.MenuSupabaseDataSource
import com.trackhub.feat_menu.data.repo.MenuRepositoryImpl
import com.trackhub.feat_menu.domain.cache.MenuCacheDataSource
import com.trackhub.feat_menu.domain.remote.MenuRemoteDataSource
import com.trackhub.feat_menu.domain.repo.MenuRepository
import com.trackhub.feat_menu.presentation.MenuViewModel
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
            remoteDataSource = get()
        )
    }

    viewModelOf(::MenuViewModel)
}