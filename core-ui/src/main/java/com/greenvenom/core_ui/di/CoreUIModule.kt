package com.greenvenom.core_ui.di

import com.greenvenom.core_ui.presentation.ScaffoldViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreUIModule = module {
    viewModelOf(::ScaffoldViewModel)
}