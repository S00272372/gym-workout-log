package com.example.gymlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymlog.R
import com.example.gymlog.ui.viewmodel.AppViewModelProvider
import com.example.gymlog.ui.viewmodel.DetailViewModel

@Composable
fun WorkoutDetailScreen(
    onEditWorkout: (Long) -> Unit,
    onAddPhoto: () -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    //rememberSaveable keeps the dialog open after a rotation
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val details = uiState.workout

    if (details == null) {
        //still loading or already deleted
        if (!uiState.isLoading) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.workout_not_found),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = details.workout.name,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = formatDate(details.workout.dateMillis),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (details.workout.notes.isNotBlank()) {
                Text(
                    text = details.workout.notes,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = stringResource(R.string.sets_title),
                style = MaterialTheme.typography.titleLarge
            )
            if (details.sets.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_sets),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                details.sets.forEach { set ->
                    Text(
                        text = stringResource(
                            R.string.set_summary,
                            set.exerciseName,
                            set.reps,
                            set.weightKg.toString()
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Button(
                onClick = { onEditWorkout(details.workout.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.edit_workout))
            }
            OutlinedButton(onClick = onAddPhoto, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.add_photo))
            }
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.delete_workout))
            }
        }
    }

    //ask before deleting because it also removes the sets and cannot be undone
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteWorkout(onDeleted)
                    }
                ) {
                    Text(stringResource(R.string.delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}