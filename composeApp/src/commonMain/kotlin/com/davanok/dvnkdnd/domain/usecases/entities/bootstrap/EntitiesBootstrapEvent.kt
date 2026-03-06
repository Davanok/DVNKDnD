package com.davanok.dvnkdnd.domain.usecases.entities.bootstrap

sealed interface EntitiesBootstrapEvent {
    object Started : EntitiesBootstrapEvent
    data class LocalChecked(val existingCount: Int, val missingCount: Int) : EntitiesBootstrapEvent
    data class RemoteLoaded(val loadedCount: Int) : EntitiesBootstrapEvent
    object Saved : EntitiesBootstrapEvent
    object Finished : EntitiesBootstrapEvent
}