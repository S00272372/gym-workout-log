package com.example.gymlog.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

//one logged workout session it works table "workouts"
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dateMillis: Long,          //when the workout happened epoch milliseconds
    val notes: String = "",
    val photoUri: String? = null   //progress photo taken with CameraX optional
)

//one exercise set that belongs to a workout table "exercise_sets"
//the foreign key means deleting a workout also deletes its sets CASCADE
@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseName: String,
    val reps: Int,
    val weightKg: Double
)

//a workout together with all of its sets one to many relation
data class WorkoutWithSets(
    @Embedded val workout: Workout,
    @Relation(parentColumn = "id", entityColumn = "workoutId")
    val sets: List<ExerciseSet>
)