package com.listocalixto.dailycosmo.ui.apod

import android.app.Application
import androidx.lifecycle.*
import com.listocalixto.dailycosmo.repository.apod.APODDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = APODDataStore(application)

    val readFromDataStore = repo.readFromDataStore.asLiveData()

    fun saveToDataStore(newStarDate: Calendar) = viewModelScope.launch(Dispatchers.IO) {
        repo.saveToDataStore(newStarDate)
    }
}

class DataStoreViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DataStoreViewModel(application) as T
    }
}
