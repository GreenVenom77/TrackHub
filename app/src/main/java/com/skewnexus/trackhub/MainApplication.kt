package com.skewnexus.trackhub

import android.app.Application
import com.greenvenom.core_navigation.di.navigationCoreModule
import com.greenvenom.feat_auth.di.authFeatureModule
import com.greenvenom.feat_menu.di.menuModule
import com.seravian.feat_local.di.localModule
import com.skewnexus.trackhub.di.appModule
import com.trackhub.feat_network.di.networkFeatureModule
import com.trackhub.feat_hub.di.hubFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            androidLogger()

            modules(
                appModule,
                networkFeatureModule,
                navigationCoreModule,
                authFeatureModule,
                hubFeatureModule,
                menuModule,
                localModule
            )
        }
    }
}