package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.DnDEntityMin

interface NewCharacterRepository {
    suspend fun getClassesMinList(source: String): List<DnDEntityMin>
}