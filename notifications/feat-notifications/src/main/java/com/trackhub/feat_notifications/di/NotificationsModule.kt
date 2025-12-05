package com.trackhub.feat_notifications.di

import com.trackhub.feat_notifications.data.remote.SupabaseHubInvitations
import com.trackhub.feat_notifications.data.repo.NotificationsRepositoryImpl
import com.trackhub.feat_notifications.domain.remote.HubInvitationsDataSource
import com.trackhub.feat_notifications.domain.repo.NotificationsRepository
import com.trackhub.feat_notifications.presentation.viewmodel.NotificationsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationsModule = module {
    single<HubInvitationsDataSource> {
        SupabaseHubInvitations(
            supabaseClient = get()
        )
    }

    single<NotificationsRepository> {
        NotificationsRepositoryImpl(
            invitationsDataSource = get()
        )
    }

    viewModelOf(::NotificationsViewModel)
}