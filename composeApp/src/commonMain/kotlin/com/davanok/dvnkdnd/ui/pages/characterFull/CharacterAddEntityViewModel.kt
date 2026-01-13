package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository

class CharacterAddEntityViewModel(
    private val entitiesType: DnDEntityTypes,
    private val browseRepository: BrowseRepository
) : ViewModel() {

}