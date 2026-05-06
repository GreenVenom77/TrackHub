package com.trackhub.feat_navigation.di

import com.greenvenom.core_navigation.domain.repos.NavigationRepository
import com.greenvenom.core_navigation.utils.AppNavigator
import com.trackhub.feat_navigation.data.repos.NavigationRepositoryImpl
import com.trackhub.feat_navigation.utils.AppNavigatorImpl
import org.koin.dsl.module

val navigationModule = module {
    single<AppNavigator> {
        AppNavigatorImpl()
    }

    single<NavigationRepository> {
        NavigationRepositoryImpl(
            appNavigator = get()
        )
    }
}