package com.listocalixto.dailycosmo.data.local

import androidx.room.*
import com.listocalixto.dailycosmo.data.model.APODEntity

@Dao
interface APODDao {

    @Query("SELECT * FROM apodentity ORDER BY date DESC")
    suspend fun getAllAPODs(): List<APODEntity>

    @Insert(onConflict= OnConflictStrategy.IGNORE)
    suspend fun saveAPOD(apod: APODEntity)

}