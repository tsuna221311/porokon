package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.GCSApiClient
import com.example.myapplication.network.UpdateWorkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class PatternEditUiState(
    val patternGrid: List<List<String>> = emptyList(),
    val selectedSymbol: String = "k",
    val selectedCell: Pair<Int, Int>? = null,
    val canUndo: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PatternEditViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternEditUiState())
    val uiState: StateFlow<PatternEditUiState> = _uiState.asStateFlow()

    private val workId: Int = checkNotNull(savedStateHandle["workId"])
    private val undoStack = mutableListOf<List<List<String>>>()

    init {
        loadPatternForEdit(workId)
    }

    private fun loadPatternForEdit(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val workResponse = ApiClient.service.getOneWork(id)
                val workBody = workResponse.body()
                if (!workResponse.isSuccessful || workBody == null || workBody.work_url.isBlank()) {
                    throw Exception("Failed to get work info")
                }

                val csvResponse = GCSApiClient.service.downloadCsv(workBody.work_url)
                val csvBody = csvResponse.body()
                if (!csvResponse.isSuccessful || csvBody == null) {
                    throw Exception("Failed to download CSV")
                }

                val grid = csvBody.string().lines().mapNotNull { if (it.isNotBlank()) it.split(",") else null }
                _uiState.update { it.copy(patternGrid = grid, isLoading = false, error = null) }

            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Failed to load pattern", e)
                _uiState.update { it.copy(error = "編み図の読み込みに失敗しました", isLoading = false) }
            }
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        pushToUndoStack()
        val newGrid = _uiState.value.patternGrid.map { it.toMutableList() }.toMutableList()
        newGrid[row][col] = _uiState.value.selectedSymbol
        _uiState.update { it.copy(patternGrid = newGrid, selectedCell = Pair(row, col)) }
    }

    fun onSymbolSelected(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol) }
    }

    fun addRow() {
        pushToUndoStack()
        val currentGrid = _uiState.value.patternGrid
        val newRow = List(currentGrid.firstOrNull()?.size ?: 10) { "-" }
        _uiState.update { it.copy(patternGrid = currentGrid + listOf(newRow)) }
    }

    fun removeRow() {
        pushToUndoStack()
        val currentGrid = _uiState.value.patternGrid
        if (currentGrid.isNotEmpty()) {
            _uiState.update { it.copy(patternGrid = currentGrid.dropLast(1)) }
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val lastState = undoStack.removeAt(undoStack.lastIndex)
            _uiState.update { it.copy(patternGrid = lastState, canUndo = undoStack.isNotEmpty()) }
        }
    }

    fun savePattern() {
        viewModelScope.launch {
            try {
                val csvContent = _uiState.value.patternGrid.joinToString("\n") { it.joinToString(",") }
                val requestBody = csvContent.toRequestBody("text/csv".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "updated_pattern.csv", requestBody)

                val uploadResponse = ApiClient.service.uploadCsv(multipartBody)
                val newUrl = uploadResponse.body()?.csv_url ?: throw Exception("CSV upload failed")

                val updateRequest = UpdateWorkRequest(work_url = newUrl)
                val updateResponse = ApiClient.service.updateWork(workId, updateRequest)
                if (!updateResponse.isSuccessful) {
                    throw Exception("Work update failed")
                }

                // 保存成功時はエラークリア
                _uiState.update { it.copy(error = null) }

            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Failed to save pattern", e)
                _uiState.update { it.copy(error = "編み図の保存に失敗しました") }
            }
        }
    }

    private fun pushToUndoStack() {
        undoStack.add(_uiState.value.patternGrid)
        _uiState.update { it.copy(canUndo = true) }
    }
}