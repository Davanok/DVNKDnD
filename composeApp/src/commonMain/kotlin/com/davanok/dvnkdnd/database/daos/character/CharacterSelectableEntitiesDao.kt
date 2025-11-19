package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.database.entities.character.DbCharacterProficiency

@Dao
interface CharacterSelectableEntitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterFeats(feats: List<DbCharacterFeat>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedProficiencies(proficiencies: List<DbCharacterProficiency>)
}