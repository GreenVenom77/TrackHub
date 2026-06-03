package com.trackhub.feat_navigation.di

import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.trackhub.feat_navigation.data.AppNavigator
import com.trackhub.feat_navigation.data.repos.NavigationRepositoryImpl
import org.koin.dsl.module

val navigationModule = module {
    single {
        AppNavigator()
    }

    single<NavigationRepository> {
        NavigationRepositoryImpl(
            appNavigator = get()
        )
    }
}