package com.greenvenom.feat_menu.di

import com.greenvenom.core_menu.data.AppPrefStateRepository
import com.greenvenom.feat_menu.presentation.MenuViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val menuModule = module {
    singleOf(::MenuViewModel)
    singleOf(::AppPrefStateRepository)
}