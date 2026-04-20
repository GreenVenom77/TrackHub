package com.trackhub.feat_network.di

import com.greenvenom.core_network.domain.SessionRepository
import com.trackhub.feat_network.data.ClientFactory
import com.trackhub.feat_network.data.repository.SupabaseSessionRepository
import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module

val networkFeatureModule = module {
    single<SupabaseClient> { ClientFactory.buildSupabaseClient() }

    single<SessionRepository>(createdAtStart = true) {
        SupabaseSessionRepository(
            supabaseClient = get(),
            profileDao = get()
        )
    }
}