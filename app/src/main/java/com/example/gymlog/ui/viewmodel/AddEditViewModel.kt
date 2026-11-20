package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.ExerciseSet
import com.example.gymlog.data.Workout
import com.example.gymlog.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//one exercise set typed in the form that is not saved to the database yet
data class SetDraft(
    val exerciseName: String,
    val reps: Int,
    val weightKg: Double
)

//the state of the form and the screen only draws this and it never changes it directly
data class AddEditUiState(
    val name: String = "",
    val notes: String = "",
    val sets: List<SetDraft> = emptyList(),
    val newExercise: String = "",
    val newReps: String = "",
    val newWeight: String = "",
    val nameError: Boolean = false,
    val setError: Boolean = false,
    val isSaving: Boolean = false
)

class AddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository
) : ViewModel() {

    //-1 means a new workout and any other value is the id of the workout being edited
    private val workoutId: Long = savedStateHandle.get<Long>("workoutId") ?: -1L

    //private writable state and public read only state
    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    //kept so the date and the photo are not lost when an edited workout is saved
    private var originalWorkout: Workout? = null
    private var originalSets: List<ExerciseSet> = emptyList()

    init {
        //edit mode: load the workout once and fill the form
        if (workoutId != -1L) {
            viewModelScope.launch {
                val loaded = repository.observeWorkoutWithSets(workoutId).first()
                if (loaded != null) {
                    originalWorkout = loaded.workout
                    originalSets = loaded.sets
                    _uiState.update {
                        it.copy(
                            name = loaded.workout.name,
                            notes = loaded.workout.notes,
                            sets = loaded.sets.map { s ->
                                SetDraft(s.exerciseName, s.reps, s.weightKg)
                            }
                        )
                    }
                }
            }
        }
    }

    //events from the screen
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, nameError = false) }
    }

    fun onNotesChange(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun onNewExerciseChange(value: String) {
        _uiState.update { it.copy(newExercise = value, setError = false) }
    }

    fun onNewRepsChange(value: String) {
        _uiState.update { it.copy(newReps = value, setError = false) }
    }

    fun onNewWeightChange(value: String) {
        _uiState.update { it.copy(newWeight = value, setError = false) }
    }

    fun onAddSet() {
        val s = _uiState.value
        val reps = s.newReps.toIntOrNull()
        //an empty weight is allowed and counts as 0 for bodyweight exercises
        val weight = if (s.newWeight.isBlank()) 0.0 else s.newWeight.toDoubleOrNull()
        if (s.newExercise.isBlank() || reps == null || reps <= 0 || weight == null) {
            _uiState.update { it.copy(setError = true) }
            return
        }
        _uiState.update {
            it.copy(
                sets = it.sets + SetDraft(s.newExercise.trim(), reps, weight),
                newExercise = "",
                newReps = "",
                newWeight = "",
                setError = false
            )
        }
    }

    fun onRemoveSet(index: Int) {
        _uiState.update { it.copy(sets = it.sets.filterIndexed { i, _ -> i != index }) }
    }

    fun save(onSaved: () -> Unit) {
        val current = _uiState.value
        if (current.isSaving) return
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val original = originalWorkout
            if (workoutId != -1L && original == null) {
                //the workout to edit no longer exists so there is nothing to save
                onSaved()
                return@launch
            }
            val savedId: Long
            if (original == null) {
                //create
                savedId = repository.addWorkout(
                    Workout(
                        name = current.name.trim(),
                        dateMillis = System.currentTimeMillis(),
                        notes = current.notes.trim()
                    )
                )
            } else {
                //update it keeps the id, date and photo and replace the sets
                repository.updateWorkout(
                    original.copy(name = current.name.trim(), notes = current.notes.trim())
                )
                originalSets.forEach { repository.deleteSet(it) }
                savedId = original.id
            }
            current.sets.forEach { draft ->
                repository.addSet(
                    ExerciseSet(
                        workoutId = savedId,
                        exerciseName = draft.exerciseName,
                        reps = draft.reps,
                        weightKg = draft.weightKg
                    )
                )
            }
            onSaved()
        }
    }
}