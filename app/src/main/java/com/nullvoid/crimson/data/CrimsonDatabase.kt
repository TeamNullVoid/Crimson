package com.nullvoid.crimson.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nullvoid.crimson.customs.Constant
import com.nullvoid.crimson.data.dao.CrimsonUserDao
import com.nullvoid.crimson.data.dao.GeofenceDao
import com.nullvoid.crimson.data.model.GeofenceModel
import com.nullvoid.crimson.data.model.LocalCrimsonUser

@Database(entities = [LocalCrimsonUser::class, GeofenceModel::class], version = 1)
abstract class CrimsonDatabase : RoomDatabase() {

    abstract val crimsonUserDao: CrimsonUserDao
    abstract val geofenceDao: GeofenceDao

    companion object {
        @Volatile
        private var INSTANCE: CrimsonDatabase? = null

        fun getInstance(context: Context): CrimsonDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CrimsonDatabase::class.java,
                        Constant.DB_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}