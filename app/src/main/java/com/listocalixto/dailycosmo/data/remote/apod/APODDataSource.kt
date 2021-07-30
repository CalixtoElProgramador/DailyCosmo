package com.listocalixto.dailycosmo.data.remote.apod

import com.listocalixto.dailycosmo.application.AppConstants
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.repository.apod.APODWebService

class APODDataSource (private val webService: APODWebService) {

    suspend fun getResults(endDate: String, startDate: String): List<APOD> = webService.getResults(
        AppConstants.API_KEY,
        startDate,
        endDate
    )

}