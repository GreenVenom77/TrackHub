package com.skewnexus.trackhub

import android.app.Application
import com.greenvenom.core_ui.di.coreUIModule
import com.greenvenom.core_util.di.coreUtilModule
import com.greenvenom.feat_auth.di.authModule
import com.skewnexus.trackhub.di.appModule
import com.trackhub.feat_hub.di.hubModule
import com.trackhub.feat_local.di.localModule
import com.trackhub.feat_menu.di.menuModule
import com.trackhub.feat_navigation.di.navigationModule
import com.trackhub.feat_network.di.networkModule
import com.trackhub.feat_notifications.di.notificationsModule
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
                coreUIModule,
                networkModule,
                navigationModule,
                authModule,
                hubModule,
                menuModule,
                notificationsModule,
                localModule,
                coreUtilModule
            )
        }
    }
}