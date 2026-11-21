package com.example.gymlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.gymlog.data.Workout
import com.example.gymlog.ui.viewmodel.AppViewModelProvider
import com.example.gymlog.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onStartWorkout: () -> Unit,
    onAddWorkout: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    //the screen observes the viewmodel and redraws when the state changes
    val uiState by viewModel.uiState.collectAsState()

    if (isWide) {
        //wide window actions on the left and recent workouts on the right
        //each side scrolls on its own so nothing is cut off in landscape
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeActions(uiState.workoutsLast7Days, onStartWorkout, onAddWorkout)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RecentWorkouts(uiState.recentWorkouts, onOpenWorkout)
            }
        }
    } else {
        //compact window everything in one scrolling column
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HomeActions(uiState.workoutsLast7Days, onStartWorkout, onAddWorkout)
            RecentWorkouts(uiState.recentWorkouts, onOpenWorkout)
        }
    }
}

//the count and the two buttons, drawn inside whatever column calls it
@Composable
private fun HomeActions(
    workoutsLast7Days: Int,
    onStartWorkout: () -> Unit,
    onAddWorkout: () -> Unit
) {
    Text(
        text = stringResource(R.string.workouts_last_7_days, workoutsLast7Days),
        style = MaterialTheme.typography.headlineMedium
    )
    Button(onClick = onStartWorkout) {
        Text(stringResource(R.string.start_workout))
    }
    OutlinedButton(onClick = onAddWorkout) {
        Text(stringResource(R.string.add_workout))
    }
}

//the heading and the latest workouts, drawn inside whatever column calls it
@Composable
private fun RecentWorkouts(
    workouts: List<Workout>,
    onOpenWorkout: (Long) -> Unit
) {
    Text(
        text = stringResource(R.string.recent_workouts),
        style = MaterialTheme.typography.titleLarge
    )
    if (workouts.isEmpty()) {
        Text(
            text = stringResource(R.string.no_workouts),
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        workouts.forEach { workout ->
            WorkoutCard(workout = workout, onClick = { onOpenWorkout(workout.id) })
        }
    }
}