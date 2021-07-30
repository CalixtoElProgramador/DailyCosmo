package com.listocalixto.dailycosmo.repository.apod

import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.data.remote.apod.APODDataSource

class APODRepositoryImpl(private val dataSource: APODDataSource): APODRepository{

    override suspend fun getResults(endDate: String, startDate: String): List<APOD> = dataSource.getResults(endDate, startDate)

}