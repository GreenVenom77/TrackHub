package com.greenvenom.feat_menu.data.remote

import com.greenvenom.core_network.data.NetworkError
import com.greenvenom.core_network.data.NetworkResult
import com.greenvenom.core_network.supabase.util.supabaseCall
import com.greenvenom.feat_menu.domain.remote.MenuRemoteDataSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

class MenuSupabaseDataSource(
    private val supabaseClient: SupabaseClient
): MenuRemoteDataSource {
    override suspend fun logoutUser(): NetworkResult<Unit, NetworkError> {
        return supabaseCall {
            supabaseClient.auth.signOut()
        }
    }
}