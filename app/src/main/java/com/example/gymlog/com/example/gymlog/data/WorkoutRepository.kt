package com.example.gymlog.data

import kotlinx.coroutines.flow.Flow

// The repository sits between the ViewModels and the DAO, so the UI never
// talks to the database directly.
class WorkoutRepository(private val workoutDao: WorkoutDao) {

    // Read
    val workouts: Flow<List<Workout>> = workoutDao.observeWorkouts()

    fun observeWorkoutWithSets(workoutId: Long): Flow<WorkoutWithSets?> =
        workoutDao.observeWorkoutWithSets(workoutId)

    suspend fun getWorkout(workoutId: Long): Workout? = workoutDao.getWorkout(workoutId)

    suspend fun countWorkoutsSince(sinceMillis: Long): Int =
        workoutDao.countWorkoutsSince(sinceMillis)

    // Create
    suspend fun addWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)

    suspend fun addSet(set: ExerciseSet): Long = workoutDao.insertSet(set)

    // Update
    suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)

    suspend fun updateSet(set: ExerciseSet) = workoutDao.updateSet(set)

    // Delete
    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)

    suspend fun deleteSet(set: ExerciseSet) = workoutDao.deleteSet(set)
}