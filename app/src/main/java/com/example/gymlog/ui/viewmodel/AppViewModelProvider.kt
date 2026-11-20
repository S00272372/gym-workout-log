package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gymlog.GymLogApplication

//builds every ViewModel and gives it the shared repository from GymLogApplication
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { HomeViewModel(gymLogApplication().workoutRepository) }
        initializer { HistoryViewModel(gymLogApplication().workoutRepository) }
        initializer { AddEditViewModel(gymLogApplication().workoutRepository) }
    }
}

//finds the GymLogApplication object inside the ViewModel creation extras
fun CreationExtras.gymLogApplication(): GymLogApplication =
    this[AndroidViewModelFactory.APPLICATION_KEY] as GymLogApplication