package com.trackhub.feat_network.data.repository

import android.util.Log
import com.greenvenom.core_menu.data.cache.dao.ProfileDao
import com.greenvenom.core_menu.data.mappers.toDomain
import com.greenvenom.core_menu.data.mappers.toEntity
import com.greenvenom.core_menu.data.remote.ProfileDto
import com.greenvenom.core_menu.domain.Profile
import com.greenvenom.core_network.data.onError
import com.greenvenom.core_network.data.onSuccess
import com.greenvenom.core_network.domain.SessionDestinations
import com.greenvenom.core_network.domain.SessionRepository
import com.greenvenom.core_network.supabase.util.extractMetadata
import com.greenvenom.core_network.supabase.util.supabaseCall
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class SupabaseSessionRepository(
    val supabaseClient: SupabaseClient,
    val profileDao: ProfileDao
): SessionRepository {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _userSessionDestination = MutableStateFlow(SessionDestinations.INITIALIZE)
    override val userSessionDestination = _userSessionDestination
        .stateIn(scope, SharingStarted.Lazily, SessionDestinations.INITIALIZE)

    init {
        collectSessionStatus()
    }

    override fun collectSessionStatus() {
        scope.launch {
            supabaseClient.auth.sessionStatus.collect {
                handleSessionStatus(it)
            }
        }
    }

    private fun handleSessionStatus(sessionStatus: SessionStatus) {
        when(sessionStatus) {
            SessionStatus.Initializing -> {  }
            is SessionStatus.Authenticated -> {
                Log.d("Session Source", sessionStatus.source.toString())
                when(sessionStatus.source) {
                    is SessionSource.Refresh,
                    is SessionSource.Storage,
                    is SessionSource.SignIn,
                    is SessionSource.SignUp -> {
                        getUserProfile()
                        _userSessionDestination.update { SessionDestinations.MAIN }
                    }
                    is SessionSource.UserChanged -> {
                        _userSessionDestination.update { SessionDestinations.AUTH }
                    }
                    else -> { Log.d("Session Source", sessionStatus.source.toString()) }
                }
            }
            is SessionStatus.NotAuthenticated -> {
                _userSessionDestination.update { SessionDestinations.AUTH }
            }
            is SessionStatus.RefreshFailure -> {
                _userSessionDestination.update { SessionDestinations.AUTH }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getUserProfile() {
        scope.launch {
            if (profileDao.getProfile() == null) {
                supabaseCall {
                    supabaseClient.from("profiles").select().decodeSingle<ProfileDto>()
                }
                    .onSuccess { profileDto ->
                        profileDao.insertProfile(profileDto.toDomain().toEntity())
                    }
                    .onError {
                        val cachedUserInfo = supabaseClient.auth.currentUserOrNull()
                        if (cachedUserInfo != null){
                            val extractedProfile = Profile(
                                cachedUserInfo.id,
                                extractMetadata(cachedUserInfo.userMetadata, "display_name") as String,
                                cachedUserInfo.email as String,
                                cachedUserInfo.createdAt.toString(),
                            )
                            profileDao.insertProfile(extractedProfile.toEntity())
                        }
                    }
            }
        }
    }
}