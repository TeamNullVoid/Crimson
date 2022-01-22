package com.nullvoid.crimson.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nullvoid.crimson.data.model.GeofenceModel



@Dao
interface GeofenceDao {

    @Query("SELECT * FROM GeofenceModel ORDER BY name")
    fun getAll(): LiveData<List<GeofenceModel>>

    @Query("DELETE FROM GeofenceModel WHERE name IS :id")
    fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: GeofenceModel)

    @Update
    fun update(model: GeofenceModel)

}