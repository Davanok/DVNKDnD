package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterProficiency

@Dao
interface CharacterSelectableEntitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterFeats(feats: List<DbCharacterFeat>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedProficiencies(proficiencies: List<DbCharacterProficiency>)
}