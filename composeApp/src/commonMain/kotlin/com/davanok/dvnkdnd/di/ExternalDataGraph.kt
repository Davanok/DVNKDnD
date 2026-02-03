package com.davanok.dvnkdnd.di

import com.davanok.dvnkdnd.BuildConfig
import com.davanok.dvnkdnd.data.remote.implementations.BrowseRepositoryImpl
import com.davanok.dvnkdnd.data.remote.implementations.ExternalKeyValueRepositoryImpl
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.repositories.remote.ExternalKeyValueRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

interface ExternalDataGraph {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Storage)
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideBrowseRepository(supabase: SupabaseClient): BrowseRepository =
        BrowseRepositoryImpl(supabase.postgrest, supabase.storage)

    val externalKeyValueRepository: ExternalKeyValueRepository
    @Binds val ExternalKeyValueRepositoryImpl.bind: ExternalKeyValueRepository
}