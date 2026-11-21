package com.example.gymlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//data access object every database operation the app needs
@Dao
interface WorkoutDao {

    //Create
    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Insert
    suspend fun insertSet(set: ExerciseSet): Long

    //Read
    //flow re emits automatically whenever the table changes
    @Query("SELECT * FROM workouts ORDER BY dateMillis DESC")
    fun observeWorkouts(): Flow<List<Workout>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun observeWorkoutWithSets(workoutId: Long): Flow<WorkoutWithSets?>

    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkout(workoutId: Long): Workout?

    //used later by the WorkManager reminder
    @Query("SELECT COUNT(*) FROM workouts WHERE dateMillis >= :sinceMillis")
    suspend fun countWorkoutsSince(sinceMillis: Long): Int

    //Update
    @Update
    suspend fun updateWorkout(workout: Workout)

    @Update
    suspend fun updateSet(set: ExerciseSet)

    //Delete
    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Delete
    suspend fun deleteSet(set: ExerciseSet)
}