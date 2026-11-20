package com.example.gymlog.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gymlog.R

@Composable
private fun PlaceholderScreen(
    @StringRes screenName: Int,
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(screenName),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.placeholder_body),
            style = MaterialTheme.typography.bodyLarge
        )
        actions()
    }
}

@Composable
fun WorkoutDetailScreen(
    onEditWorkout: () -> Unit,
    onAddPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(R.string.screen_detail, modifier) {
        Button(onClick = onEditWorkout) {
            Text(stringResource(R.string.edit_workout))
        }
        OutlinedButton(onClick = onAddPhoto) {
            Text(stringResource(R.string.add_photo))
        }
    }
}

@Composable
fun LiveSessionScreen(
    onFinishSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(R.string.screen_live_session, modifier) {
        Button(onClick = onFinishSession) {
            Text(stringResource(R.string.finish_session))
        }
    }
}

@Composable
fun CameraScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(R.string.screen_camera, modifier) {
        Button(onClick = onDone) {
            Text(stringResource(R.string.done))
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(R.string.screen_settings, modifier)
}