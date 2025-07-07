package com.greenvenom.feat_menu.di

import com.greenvenom.feat_menu.data.remote.MenuSupabaseDataSource
import com.greenvenom.feat_menu.data.repo.MenuRepositoryImpl
import com.greenvenom.feat_menu.domain.remote.MenuRemoteDataSource
import com.greenvenom.feat_menu.domain.repo.MenuRepository
import com.greenvenom.feat_menu.presentation.MenuViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val menuModule = module {
    single<MenuRemoteDataSource> {
        MenuSupabaseDataSource(
            supabaseClient = get()
        )
    }

    single<MenuRepository> {
        MenuRepositoryImpl(
            remoteDataSource = get(),
            appPrefStateRepository = get()
        )
    }

    singleOf(::MenuViewModel)
}