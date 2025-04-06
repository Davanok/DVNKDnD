package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.BuildConfig
import com.davanok.dvnkdnd.data.implementations.BrowseRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.CharactersRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.EntitiesRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.FilesRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.NewCharacterRepositoryImpl
import com.davanok.dvnkdnd.data.platform.appCacheDirectory
import com.davanok.dvnkdnd.data.platform.appDataDirectory
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.dsl.module

fun commonModule() = module {
    single<CharactersRepository> { CharactersRepositoryImpl(get()) }
    single<NewCharacterRepository> { NewCharacterRepositoryImpl(get(), get()) }
    single<FilesRepository> { FilesRepositoryImpl(appDataDirectory(), appCacheDirectory()) }
    single<EntitiesRepository> { EntitiesRepositoryImpl(get()) }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
    single<BrowseRepository> {
        val client: SupabaseClient = get()
        BrowseRepositoryImpl(client.postgrest, client.storage)
    }
}