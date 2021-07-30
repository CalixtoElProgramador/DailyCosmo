package com.listocalixto.dailycosmo.data.local

import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.data.model.APODEntity
import com.listocalixto.dailycosmo.data.model.toAPODList

class LocalAPODDataSource(private val apodDao: APODDao) {

    suspend fun getResults(): List<APOD> {
        return apodDao.getAllAPODs().toAPODList()
    }

    suspend fun saveAPOD(apodEntity: APODEntity) {
        apodDao.saveAPOD(apodEntity)
    }
}