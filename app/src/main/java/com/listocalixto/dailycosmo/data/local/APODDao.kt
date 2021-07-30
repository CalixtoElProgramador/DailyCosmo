package com.listocalixto.dailycosmo.data.local

import androidx.room.Dao
import com.listocalixto.dailycosmo.data.model.APODEntity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface APODDao {

    @Query("SELECT * FROM apodentity")
    suspend fun getAllAPODs():List<APODEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAPOD(apod: APODEntity)

}