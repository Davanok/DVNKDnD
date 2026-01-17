package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.BuildConfig
import com.davanok.dvnkdnd.data.remote.implementations.BrowseRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.CharactersRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.EntitiesRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.FilesRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.FullEntitiesRepositoryImpl
import com.davanok.dvnkdnd.data.usecase.implementations.EntitiesBootstrapperImpl
import com.davanok.dvnkdnd.data.platform.appCacheDirectory
import com.davanok.dvnkdnd.data.platform.appDataDirectory
import com.davanok.dvnkdnd.data.remote.implementations.ExternalKeyValueRepositoryImpl
import com.davanok.dvnkdnd.data.usecase.implementations.BrowseEntitiesUseCaseImpl
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.remote.ExternalKeyValueRepository
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import com.davanok.dvnkdnd.domain.usecases.entities.BrowseEntitiesUseCase
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun commonModule() = module {
    single<FilesRepository> { FilesRepositoryImpl(appDataDirectory(), appCacheDirectory()) }

    singleOf(::CharactersRepositoryImpl) bind CharactersRepository::class
    singleOf(::EntitiesRepositoryImpl) bind EntitiesRepository::class
    singleOf(::FullEntitiesRepositoryImpl) bind FullEntitiesRepository::class

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
    singleOf(::ExternalKeyValueRepositoryImpl) bind ExternalKeyValueRepository::class

    factoryOf(::EntitiesBootstrapperImpl) bind EntitiesBootstrapper::class
    factoryOf(::BrowseEntitiesUseCaseImpl) bind BrowseEntitiesUseCase::class
}