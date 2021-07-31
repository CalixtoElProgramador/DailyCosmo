package com.listocalixto.dailycosmo.repository.apod

import com.listocalixto.dailycosmo.core.InternetCheck
import com.listocalixto.dailycosmo.data.local.LocalAPODDataSource
import com.listocalixto.dailycosmo.data.model.APOD
import com.listocalixto.dailycosmo.data.model.toAPODEntity
import com.listocalixto.dailycosmo.data.remote.apod.RemoteAPODDataSource

class APODRepositoryImpl(
    private val dataSourceRemote: RemoteAPODDataSource,
    private val dataSourceLocal: LocalAPODDataSource
) : APODRepository {

    override suspend fun getResults(endDate: String, startDate: String): List<APOD> {
        return if (InternetCheck.isNetworkAvailable()) {
            dataSourceRemote.getResults(endDate, startDate).asReversed().forEach { apod ->
                dataSourceLocal.saveAPOD(apod.toAPODEntity())
            }
            dataSourceLocal.getResults()
        } else {
            dataSourceLocal.getResults()
        }
    }
}