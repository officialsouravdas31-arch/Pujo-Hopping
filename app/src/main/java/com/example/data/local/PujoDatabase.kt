package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        VisitedEntity::class,
        CrowdReportEntity::class,
        SavedRouteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PujoDatabase : RoomDatabase() {
    abstract fun pujoDao(): PujoDao

    companion object {
        @Volatile
        private var INSTANCE: PujoDatabase? = null

        fun getDatabase(context: Context): PujoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PujoDatabase::class.java,
                    "pujoplan_kolkata.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
