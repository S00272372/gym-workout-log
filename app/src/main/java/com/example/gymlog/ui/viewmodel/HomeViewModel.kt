package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.Workout
import com.example.gymlog.data.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

//everything the home screen needs to draw itself
data class HomeUiState(
    val recentWorkouts: List<Workout> = emptyList(),
    val workoutsLast7Days: Int = 0
)

class HomeViewModel(repository: WorkoutRepository) : ViewModel() {

    //the database flow is turned into a StateFlow. Whenever a workout is added,
    //changed or deleted, room emits a new list and this state updates by itself.
    val uiState: StateFlow<HomeUiState> = repository.workouts
        .map { workouts ->
            val sevenDaysAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            HomeUiState(
                recentWorkouts = workouts.take(3),
                workoutsLast7Days = workouts.count { it.dateMillis >= sevenDaysAgo }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )
}