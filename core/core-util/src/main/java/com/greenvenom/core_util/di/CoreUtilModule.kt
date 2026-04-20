package com.greenvenom.core_util.di

import com.greenvenom.core_util.phone.PhoneNumberManager
import com.greenvenom.core_util.theme.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreUtilModule = module {
    single {
        PhoneNumberManager(androidContext())
    }

    single {
        ThemeManager(androidContext())
    }
}