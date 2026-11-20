package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.Workout
import com.example.gymlog.data.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

//isLoading stops the no workouts yet message from flashing before the data arrives
data class HistoryUiState(
    val workouts: List<Workout> = emptyList(),
    val isLoading: Boolean = true
)

class HistoryViewModel(repository: WorkoutRepository) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.workouts
        .map { workouts -> HistoryUiState(workouts = workouts, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )
}