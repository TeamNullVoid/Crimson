package com.nullvoid.crimson.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nullvoid.crimson.data.model.LocalCrimsonUser

@Dao
interface CrimsonUserDao {

    @Query("SELECT * FROM LocalCrimsonUser ORDER BY userName ASC")
    fun getUsers(): LiveData<List<LocalCrimsonUser>>

    @Query("SELECT * FROM LocalCrimsonUser ORDER BY userName ASC")
    fun getAllUsers(): List<LocalCrimsonUser>

    @Query("SELECT * FROM LocalCrimsonUser WHERE userId IS :id")
    fun getUser(id: String): LiveData<LocalCrimsonUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: LocalCrimsonUser)

    @Query("DELETE FROM LocalCrimsonUser WHERE userId IS :id")
    fun delete(id: String)

    @Update
    fun update(user: LocalCrimsonUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<LocalCrimsonUser>)

    @Query("UPDATE LocalCrimsonUser SET latitude = :lat, longitude = :lon, accuracy = :acc, lastUpdate = :lu WHERE userId IS :id")
    fun update(id: String, lat: Double, lon: Double, acc: Float, lu: String)

    @Query("UPDATE LocalCrimsonUser SET inEmergency = :inEm WHERE userId IS :id")
    fun update(id: String, inEm: Boolean)

    @Query("DELETE FROM LocalCrimsonUser")
    fun clear()

}