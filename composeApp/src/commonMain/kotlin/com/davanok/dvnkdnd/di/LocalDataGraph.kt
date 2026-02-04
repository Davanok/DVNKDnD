package com.davanok.dvnkdnd.di

import com.davanok.dvnkdnd.data.local.implementations.CharactersRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.EditCharacterRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.EntitiesRepositoryImpl
import com.davanok.dvnkdnd.data.local.implementations.FullEntitiesRepositoryImpl
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.repositories.local.EditCharacterRepository
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import dev.zacsweers.metro.Binds

interface LocalDataGraph: DatabaseGraph {
    val charactersRepository: CharactersRepository
    @Binds val CharactersRepositoryImpl.bind: CharactersRepository

    val entitiesRepository: EntitiesRepository
    @Binds val EntitiesRepositoryImpl.bind: EntitiesRepository

    val fullEntitiesRepository: FullEntitiesRepository
    @Binds val FullEntitiesRepositoryImpl.bind: FullEntitiesRepository

    val editCharacterRepository: EditCharacterRepository
    @Binds val EditCharacterRepositoryImpl.bind: EditCharacterRepository
}