package com.example.gymlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.ui.viewmodel.AppViewModelProvider
import com.example.gymlog.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onStartWorkout: () -> Unit,
    onAddWorkout: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //the screen observes the ViewModel StateFlow and redraws when it changes
    val uiState by viewModel.uiState.collectAsState()

    //scrolls so nothing is cut off in landscape
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.workouts_last_7_days, uiState.workoutsLast7Days),
            style = MaterialTheme.typography.headlineMedium
        )
        Button(onClick = onStartWorkout) {
            Text(stringResource(R.string.start_workout))
        }
        OutlinedButton(onClick = onAddWorkout) {
            Text(stringResource(R.string.add_workout))
        }
        Text(
            text = stringResource(R.string.recent_workouts),
            style = MaterialTheme.typography.titleLarge
        )
        if (uiState.recentWorkouts.isEmpty()) {
            Text(
                text = stringResource(R.string.no_workouts),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            uiState.recentWorkouts.forEach { workout ->
                WorkoutCard(workout = workout, onClick = { onOpenWorkout(workout.id) })
            }
        }
    }
}