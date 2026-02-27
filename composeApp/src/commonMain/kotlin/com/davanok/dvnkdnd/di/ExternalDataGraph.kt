package com.davanok.dvnkdnd.di

import com.davanok.dvnkdnd.BuildConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlin.time.Duration.Companion.seconds

interface ExternalDataGraph {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSupabaseClient(): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        requestTimeout = 30.seconds
        install(Postgrest)
        install(Storage)
    }

    @Provides
    fun provideSupabasePostgrest(supabase: SupabaseClient): Postgrest = supabase.postgrest
    @Provides
    fun provideSupabaseStorage(supabase: SupabaseClient): Storage = supabase.storage
}