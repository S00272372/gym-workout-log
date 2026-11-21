package com.example.gymlog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//the room database lists the tables entities and exposes the DAO
@Database(
    entities = [Workout::class, ExerciseSet::class],
    version = 1,
    exportSchema = false
)
abstract class GymLogDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var instance: GymLogDatabase? = null

        //returns the single shared database, creating it the first time it is needed
        fun getDatabase(context: Context): GymLogDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GymLogDatabase::class.java,
                    "gymlog_database"
                ).build().also { instance = it }
            }
        }
    }
}