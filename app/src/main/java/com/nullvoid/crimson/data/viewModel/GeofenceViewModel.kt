package com.nullvoid.crimson.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nullvoid.crimson.data.CrimsonDatabase
import com.nullvoid.crimson.data.model.GeofenceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceViewModel(application: Application) : AndroidViewModel(application) {

    private val list: LiveData<List<GeofenceModel>>
    private val db: CrimsonDatabase = CrimsonDatabase.getInstance(application.applicationContext)

    init {
        list = db.geofenceDao.getAll()
    }

    fun insert(model: GeofenceModel) {
        viewModelScope.launch(Dispatchers.IO) {
            db.geofenceDao.insert(model)
        }
    }

    fun getPlaces(): LiveData<List<GeofenceModel>> {
        return list
    }

}