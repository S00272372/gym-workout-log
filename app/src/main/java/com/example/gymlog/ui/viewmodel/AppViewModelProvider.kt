package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gymlog.GymLogApplication

//builds every viewmodel and gives it the shared repository from GymLogApplication
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(gymLogApplication().workoutRepository) }
        initializer { HistoryViewModel(gymLogApplication().workoutRepository) }
        initializer { SettingsViewModel(gymLogApplication().settingsRepository) }
        //the saved state handle holds the navigation arguments such as workoutId
        initializer {
            DetailViewModel(this.createSavedStateHandle(), gymLogApplication().workoutRepository)
        }
        initializer {
            AddEditViewModel(this.createSavedStateHandle(), gymLogApplication().workoutRepository)
        }
    }
}

//finds the GymLogApplication object inside the viewmodel creation extras
fun CreationExtras.gymLogApplication(): GymLogApplication =
    this[AndroidViewModelFactory.APPLICATION_KEY] as GymLogApplication