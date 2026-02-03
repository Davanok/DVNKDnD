package com.davanok.dvnkdnd.di

import com.davanok.dvnkdnd.AppClass
import com.davanok.dvnkdnd.data.local.implementations.FilesRepositoryImpl
import com.davanok.dvnkdnd.data.usecase.implementations.BrowseEntitiesUseCaseImpl
import com.davanok.dvnkdnd.data.usecase.implementations.EntitiesBootstrapperImpl
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import com.davanok.dvnkdnd.domain.usecases.entities.BrowseEntitiesUseCase
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import dev.zacsweers.metro.Binds
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppGraph : LocalDataGraph, ExternalDataGraph, PlatformGraph, ViewModelGraph {
    val app: AppClass

    val filesRepository: FilesRepository
    @Binds val FilesRepositoryImpl.bind: FilesRepository

    val entitiesBootstrapper: EntitiesBootstrapper
    @Binds val EntitiesBootstrapperImpl.bind: EntitiesBootstrapper

    val browseEntitiesUseCase: BrowseEntitiesUseCase
    @Binds val BrowseEntitiesUseCaseImpl.bind: BrowseEntitiesUseCase
}