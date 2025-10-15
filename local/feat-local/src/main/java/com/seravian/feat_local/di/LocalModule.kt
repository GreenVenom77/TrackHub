package com.seravian.feat_local.di

import androidx.room.Room
import com.seravian.core_local.domain.AppPrefsDataSource
import com.seravian.feat_local.data.DataStoreAppPrefs
import com.seravian.feat_local.data.db.TrackHubDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single {
        get<TrackHubDatabase>().hubDao
    }

    single {
        get<TrackHubDatabase>().itemDao
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            TrackHubDatabase::class.java,
            name = "trackhub.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single<AppPrefsDataSource> {
        DataStoreAppPrefs(androidContext())
    }
}