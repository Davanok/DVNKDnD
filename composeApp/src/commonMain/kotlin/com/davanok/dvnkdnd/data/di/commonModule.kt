package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.data.implementations.CharactersListRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.FilesRepositoryImpl
import com.davanok.dvnkdnd.data.implementations.NewCharacterRepositoryImpl
import com.davanok.dvnkdnd.data.platform.appCacheDirectory
import com.davanok.dvnkdnd.data.platform.appDataDirectory
import com.davanok.dvnkdnd.data.repositories.CharactersListRepository
import com.davanok.dvnkdnd.data.repositories.FilesRepository
import com.davanok.dvnkdnd.data.repositories.NewCharacterRepository
import org.koin.dsl.module

fun commonModule() = module {
    single<CharactersListRepository> { CharactersListRepositoryImpl(get()) }
    single<NewCharacterRepository> { NewCharacterRepositoryImpl(get()) }
    single<FilesRepository> { (FilesRepositoryImpl(appDataDirectory(), appCacheDirectory())) }
}