package com.listocalixto.dailycosmo.repository.apod

import com.listocalixto.dailycosmo.data.model.APOD

interface APODRepository {

    suspend fun getResults(endDate: String, startDate: String): List<APOD>

}