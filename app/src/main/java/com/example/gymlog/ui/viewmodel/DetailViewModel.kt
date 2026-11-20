package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.WorkoutRepository
import com.example.gymlog.data.WorkoutWithSets
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//workout is null while loading and after the workout has been deleted
data class DetailUiState(
    val workout: WorkoutWithSets? = null,
    val isLoading: Boolean = true
)

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: WorkoutRepository
) : ViewModel() {

    //the id comes from the route Detail/{workoutId}
    private val workoutId: Long = checkNotNull(savedStateHandle.get<Long>("workoutId"))

    //room emits again after every change so an edit shows up here without any extra code
    val uiState: StateFlow<DetailUiState> = repository.observeWorkoutWithSets(workoutId)
        .map { DetailUiState(workout = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailUiState()
        )

    //delete first and leave the screen after, otherwise the viewmodel is cleared before the delete finishes
    fun deleteWorkout(onDeleted: () -> Unit) {
        val current = uiState.value.workout?.workout ?: return
        viewModelScope.launch {
            repository.deleteWorkout(current)
            onDeleted()
        }
    }
}