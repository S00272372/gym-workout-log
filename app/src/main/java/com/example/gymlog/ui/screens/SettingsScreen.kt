package com.example.gymlog.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import com.example.gymlog.R
import com.example.gymlog.data.ThemeMode
import com.example.gymlog.ui.viewmodel.AppViewModelProvider
import com.example.gymlog.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val reminderEnabled by viewModel.reminderEnabled.collectAsState()
    val testStatus by viewModel.testStatus.collectAsState()
    val context = LocalContext.current

    //android 13 and above shows a dialog that asks the user to allow notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    val askForNotificationPermission = {
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.theme_title),
            style = MaterialTheme.typography.titleLarge
        )
        ThemeOption(R.string.theme_system, ThemeMode.System, themeMode, viewModel::onThemeSelected)
        ThemeOption(R.string.theme_light, ThemeMode.Light, themeMode, viewModel::onThemeSelected)
        ThemeOption(R.string.theme_dark, ThemeMode.Dark, themeMode, viewModel::onThemeSelected)

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = stringResource(R.string.reminders_title),
            style = MaterialTheme.typography.titleLarge
        )
        //the whole row can be tapped to turn the reminder on or off
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = reminderEnabled,
                    onValueChange = { enabled ->
                        if (enabled) askForNotificationPermission()
                        viewModel.onReminderToggled(enabled)
                    },
                    role = Role.Switch
                )
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reminder_switch_label),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = reminderEnabled, onCheckedChange = null)
        }
        OutlinedButton(
            onClick = {
                askForNotificationPermission()
                viewModel.onSendTestReminder()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.reminder_test_button))
        }
        //shows what the one time test work is doing
        val statusLabel = testStatus?.let { workStatusLabel(it) }
        if (statusLabel != null) {
            Text(
                text = stringResource(R.string.reminder_status_label, stringResource(statusLabel)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//turns a work state into a short word for the screen
@StringRes
private fun workStatusLabel(state: WorkInfo.State): Int = when (state) {
    WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> R.string.work_status_waiting
    WorkInfo.State.RUNNING -> R.string.work_status_running
    WorkInfo.State.SUCCEEDED -> R.string.work_status_sent
    WorkInfo.State.FAILED -> R.string.work_status_failed
    WorkInfo.State.CANCELLED -> R.string.work_status_cancelled
}

//one row with a radio button, the whole row can be tapped
@Composable
private fun ThemeOption(
    @StringRes label: Int,
    mode: ThemeMode,
    selectedMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = mode == selectedMode,
                onClick = { onSelect(mode) },
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = mode == selectedMode, onClick = null)
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}