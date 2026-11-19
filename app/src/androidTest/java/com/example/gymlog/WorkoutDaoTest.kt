package com.example.gymlog

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gymlog.data.ExerciseSet
import com.example.gymlog.data.GymLogDatabase
import com.example.gymlog.data.Workout
import com.example.gymlog.data.WorkoutDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

//instrumented test runs on the emulator against a temporary in memory database
@RunWith(AndroidJUnit4::class)
class WorkoutDaoTest {

    private lateinit var database: GymLogDatabase
    private lateinit var dao: WorkoutDao

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, GymLogDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.workoutDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertWorkout_thenRead_returnsIt() = runBlocking {
        dao.insertWorkout(Workout(name = "Push day", dateMillis = 1000L))

        val workouts = dao.observeWorkouts().first()

        assertEquals(1, workouts.size)
        assertEquals("Push day", workouts[0].name)
    }

    @Test
    fun updateWorkout_changesStoredValue() = runBlocking {
        val id = dao.insertWorkout(Workout(name = "Push day", dateMillis = 1000L))
        val saved = dao.getWorkout(id)!!

        dao.updateWorkout(saved.copy(name = "Pull day"))

        assertEquals("Pull day", dao.getWorkout(id)?.name)
    }

    @Test
    fun deleteWorkout_removesIt() = runBlocking {
        val id = dao.insertWorkout(Workout(name = "Leg day", dateMillis = 1000L))
        val saved = dao.getWorkout(id)!!

        dao.deleteWorkout(saved)

        assertNull(dao.getWorkout(id))
        assertEquals(0, dao.observeWorkouts().first().size)
    }

    @Test
    fun workoutWithSets_returnsAllSets() = runBlocking {
        val id = dao.insertWorkout(Workout(name = "Push day", dateMillis = 1000L))
        dao.insertSet(ExerciseSet(workoutId = id, exerciseName = "Bench press", reps = 8, weightKg = 60.0))
        dao.insertSet(ExerciseSet(workoutId = id, exerciseName = "Bench press", reps = 6, weightKg = 65.0))

        val result = dao.observeWorkoutWithSets(id).first()

        assertEquals(2, result?.sets?.size)
    }

    @Test
    fun deleteWorkout_alsoDeletesItsSets() = runBlocking {
        val id = dao.insertWorkout(Workout(name = "Push day", dateMillis = 1000L))
        dao.insertSet(ExerciseSet(workoutId = id, exerciseName = "Bench press", reps = 8, weightKg = 60.0))
        val saved = dao.getWorkout(id)!!

        dao.deleteWorkout(saved)

        //the workout is gone, so the relation query returns null CASCADE removed the set
        assertNull(dao.observeWorkoutWithSets(id).first())
    }
}