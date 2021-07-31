package com.listocalixto.dailycosmo.repository.apod

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PREFERENCE_NAME = "preferences_01"

@SuppressLint("SimpleDateFormat")
private val sdf = SimpleDateFormat("yyyy-MM-dd")

class APODDataStore(context: Context) {

    private object PreferencesKeys {
        val newStartDate = preferencesKey<String>("new_star_date")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(name = PREFERENCE_NAME)

    suspend fun saveToDataStore(newStarDate: Calendar) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.newStartDate] = sdf.format(newStarDate.time)
        }
        Log.d("DataStore", "Se ha guardado la fecha: ${sdf.format(newStarDate.time)}")
    }

    val readFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val today: Calendar = Calendar.getInstance()
            val startDate: Calendar = Calendar.getInstance().apply {
                set(
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DATE)
                )
                add(Calendar.DATE, -10)
            }
            val newStartDate =
                preferences[PreferencesKeys.newStartDate] ?: sdf.format(startDate.time)
            Log.d("DataStore", "Se está leyendo la fecha: $newStartDate")
            newStartDate
        }

}