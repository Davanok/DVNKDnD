package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.DnDEntityMin
import kotlinx.coroutines.flow.Flow

interface NewCharacterRepository {
    suspend fun getClassesMinList(source: String): List<DnDEntityMin>
}