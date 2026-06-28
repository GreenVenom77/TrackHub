package com.greenvenom.feat_auth.di

import com.greenvenom.core_auth.data.repository.EmailStateRepository
import com.greenvenom.feat_auth.data.remote.AuthSupabaseDataSource
import com.greenvenom.feat_auth.data.repo.AuthRepositoryImpl
import com.greenvenom.feat_auth.domain.remote.AuthRemoteDataSource
import com.greenvenom.feat_auth.domain.repo.AuthRepository
import com.greenvenom.feat_auth.presentation.login.LoginViewModel
import com.greenvenom.feat_auth.presentation.otp.OtpViewModel
import com.greenvenom.feat_auth.presentation.register.RegisterViewModel
import com.greenvenom.feat_auth.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthRemoteDataSource> {
        AuthSupabaseDataSource(
            supabaseClient = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            remoteDataSource = get()
        )
    }

    single { EmailStateRepository() }

    viewModel { LoginViewModel(authRepository = get()) }
    viewModel { RegisterViewModel(emailStateRepository = get(), authRepository = get()) }
    viewModel { OtpViewModel(emailStateRepository = get(), authRepository = get() ) }
    viewModel { ResetPasswordViewModel(emailStateRepository = get(), authRepository = get()) }
}