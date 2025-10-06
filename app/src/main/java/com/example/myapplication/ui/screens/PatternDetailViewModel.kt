package com.example.myapplication.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Work
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * UI State for the PatternDetailScreen.
 * It holds all the data needed for the UI.
 */
data class PatternUiState(
    val work: Work? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PatternDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternUiState())
    val uiState: StateFlow<PatternUiState> = _uiState.asStateFlow()

    private val workId: Int = checkNotNull(savedStateHandle["workId"])

    init {
        loadWork(workId)
    }

    private fun loadWork(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Replace this with your actual API call to fetch data.
                val dummyWork = Work(
                    id = id,
                    title = "Sample Pattern (ID: $id)",
                    description = "A basic 2x2 rib stitch scarf.",
                    work_url = "",
                    raw_index = 5,
                    stitch_index = 10,
                    is_completed = false,
                    completed_at = null,
                    created_at = Instant.now().toString(),
                    updated_at = Instant.now().toString()
                )
                _uiState.update {
                    it.copy(work = dummyWork, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to load the pattern.", isLoading = false)
                }
            }
        }
    }

    // --- Counter Actions ---

    fun incrementRow() {
        _uiState.update { currentState ->
            currentState.work?.let { currentWork ->
                // TODO: Add API call to update the server
                val updatedWork = currentWork.copy(raw_index = currentWork.raw_index + 1)
                currentState.copy(work = updatedWork)
            } ?: currentState
        }
    }

    fun decrementRow() {
        _uiState.update { currentState ->
            currentState.work?.let { currentWork ->
                if (currentWork.raw_index > 0) {
                    // TODO: Add API call to update the server
                    val updatedWork = currentWork.copy(raw_index = currentWork.raw_index - 1)
                    currentState.copy(work = updatedWork)
                } else {
                    currentState
                }
            } ?: currentState
        }
    }

    fun incrementStitch() {
        _uiState.update { currentState ->
            currentState.work?.let { currentWork ->
                // TODO: Add API call to update the server
                val updatedWork = currentWork.copy(stitch_index = currentWork.stitch_index + 1)
                currentState.copy(work = updatedWork)
            } ?: currentState
        }
    }

    fun decrementStitch() {
        _uiState.update { currentState ->
            currentState.work?.let { currentWork ->
                if (currentWork.stitch_index > 0) {
                    // TODO: Add API call to update the server
                    val updatedWork = currentWork.copy(stitch_index = currentWork.stitch_index - 1)
                    currentState.copy(work = updatedWork)
                } else {
                    currentState
                }
            } ?: currentState
        }
    }
}