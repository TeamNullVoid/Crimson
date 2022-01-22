package com.nullvoid.crimson.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nullvoid.crimson.data.CrimsonDatabase
import com.nullvoid.crimson.data.model.CrimsonExtras
import com.nullvoid.crimson.data.model.LocalCrimsonUser
import com.nullvoid.crimson.data.model.LocationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CrimsonUserViewModel(application: Application) : AndroidViewModel(application) {

    private val users: LiveData<List<LocalCrimsonUser>>
    private val userDao = CrimsonDatabase.getInstance(application).crimsonUserDao

    init {
        users = userDao.getUsers()
    }

    fun getUsers(): LiveData<List<LocalCrimsonUser>> {
        return users
    }

    fun getUser(id: String): LiveData<LocalCrimsonUser> {
        return userDao.getUser(id)
    }

    fun remove(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.delete(id)
        }
    }

    fun update(id: String, extras: CrimsonExtras) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.update(id, extras.inEmergency)
        }
    }

    fun update(id: String, loc: LocationExtras) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.update(id, loc.latitude!!, loc.longitude!!, loc.accuracy!!, loc.lastUpdate!!)
        }
    }

}