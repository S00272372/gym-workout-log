package com.example.gymlog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlog.data.Workout
import com.example.gymlog.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//the state of the form and the screen only draws this and it never changes it directly
data class AddEditUiState(
    val name: String = "",
    val notes: String = "",
    val nameError: Boolean = false,
    val isSaving: Boolean = false
)

class AddEditViewModel(private val repository: WorkoutRepository) : ViewModel() {

    //private writable state, public read-only state unidirectional data flow
    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    //events from the screen
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, nameError = false) }
    }

    fun onNotesChange(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun save(onSaved: () -> Unit) {
        val current = _uiState.value
        if (current.isSaving) return
        if (current.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.addWorkout(
                Workout(
                    name = current.name.trim(),
                    dateMillis = System.currentTimeMillis(),
                    notes = current.notes.trim()
                )
            )
            onSaved()
        }
    }
}