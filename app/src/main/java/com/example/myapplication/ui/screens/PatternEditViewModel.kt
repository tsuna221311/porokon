package com.example.myapplication.ui.screens

import android.util.Base64
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.UpdateWorkRequest
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.GCSApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// UIの状態定義
sealed interface PatternEditUiState {
    object Loading : PatternEditUiState
    data class Success(
        val patternGrid: List<List<String>>,
        val selectedSymbol: String = "k",
        val selectedCell: Pair<Int, Int>? = null,
        val canUndo: Boolean = false
    ) : PatternEditUiState
    data class SaveSuccess(val finalFileName: String) : PatternEditUiState
    data class Error(val message: String) : PatternEditUiState
}

class PatternEditViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow<PatternEditUiState>(PatternEditUiState.Loading)
    val uiState: StateFlow<PatternEditUiState> = _uiState.asStateFlow()

    private val workId: Int? = savedStateHandle["workId"]
    private val initialCsvContent: String? = savedStateHandle["csvContent"]
    private var originalWork: Work? = null
    private val undoStack = mutableListOf<List<List<String>>>()

    init {
        when {
            workId != null -> loadPatternForEdit(workId)
            initialCsvContent != null -> {
                try {
                    // Base64文字列をデコードして元のCSV文字列に戻す
                    val decodedBytes = Base64.decode(initialCsvContent, Base64.NO_WRAP)
                    val csvText = String(decodedBytes, Charsets.UTF_8)

                    // デコードしたCSVテキストをグリッドに変換
                    val grid = csvText.lines().mapNotNull { if (it.isNotBlank()) it.split(",") else null }

                    // ★★★ ここから修正箇所 ★★★
                    // グリッドの最初の2行を削除
                    val cleanedGrid = grid.drop(2)

                    // 2行削除した後のグリッドでUIの状態を更新
                    _uiState.value = PatternEditUiState.Success(patternGrid = cleanedGrid)
                    // ★★★ ここまで修正箇所 ★★★

                } catch (e: Exception) {
                    Log.e("PatternEditViewModel", "Failed to decode Base64 content", e)
                    _uiState.value = PatternEditUiState.Error("不正なデータ形式です。")
                }
            }
            else -> _uiState.value = PatternEditUiState.Error("必要な情報がありません。")
        }
    }

    private fun loadPatternForEdit(id: Int) {
        viewModelScope.launch {
            try {
                val workResponse = ApiClient.service.getOneWork(id)
                val workBody = workResponse.body()
                if (!workResponse.isSuccessful || workBody == null || workBody.work_url.isNullOrBlank()) {
                    throw Exception("Failed to get work info or work_url is empty")
                }
                originalWork = workBody
                loadNewPatternFromUrl(workBody.work_url)
            } catch (e: Exception) {
                _uiState.value = PatternEditUiState.Error("編み図の読み込みに失敗しました。")
            }
        }
    }

    private fun loadNewPatternFromUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = PatternEditUiState.Loading
            try {
                val csvResponse = GCSApiClient.service.downloadCsv(url)
                val csvBody = csvResponse.body()
                if (!csvResponse.isSuccessful || csvBody == null) { throw Exception("Failed to download CSV") }
                val grid = csvBody.string().lines().mapNotNull { if (it.isNotBlank()) it.split(",") else null }
                _uiState.value = PatternEditUiState.Success(patternGrid = grid)
            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Error loading pattern from URL", e)
                _uiState.value = PatternEditUiState.Error("編み図データの読み込みに失敗しました。")
            }
        }
    }

    fun onCellClicked(row: Int, col: Int) {
        (_uiState.value as? PatternEditUiState.Success)?.let {
            pushToUndoStack(it.patternGrid)
            val newGrid = it.patternGrid.map { r -> r.toMutableList() }.toMutableList()
            newGrid[row][col] = it.selectedSymbol
            _uiState.value = it.copy(patternGrid = newGrid, selectedCell = Pair(row, col))
        }
    }
    fun onSymbolSelected(symbol: String) {
        (_uiState.value as? PatternEditUiState.Success)?.let { _uiState.value = it.copy(selectedSymbol = symbol) }
    }
    fun addRow() {
        (_uiState.value as? PatternEditUiState.Success)?.let {
            pushToUndoStack(it.patternGrid)
            val newRow = List(it.patternGrid.firstOrNull()?.size ?: 10) { "-" }
            _uiState.value = it.copy(patternGrid = it.patternGrid + listOf(newRow))
        }
    }
    fun removeRow() {
        (_uiState.value as? PatternEditUiState.Success)?.let {
            if (it.patternGrid.isNotEmpty()) {
                pushToUndoStack(it.patternGrid)
                _uiState.value = it.copy(patternGrid = it.patternGrid.dropLast(1))
            }
        }
    }
    fun undo() {
        if (undoStack.isNotEmpty()) {
            val lastState = undoStack.removeAt(undoStack.lastIndex)
            _uiState.value = PatternEditUiState.Success(patternGrid = lastState, canUndo = undoStack.isNotEmpty())
        }
    }
    fun savePattern() {
        val currentState = _uiState.value
        if (currentState !is PatternEditUiState.Success) return
        val original = originalWork

        viewModelScope.launch {
            try {
                val csvContent = currentState.patternGrid.joinToString("\n") { it.joinToString(",") }
                val requestBody = csvContent.toRequestBody("text/csv".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "fixed_pattern.csv", requestBody)
                val uploadResponse = ApiClient.service.uploadFixedCsv(multipartBody)
                val finalFileName = uploadResponse.body()?.file_name ?: throw Exception("fix-csv API failed")

                if (workId != null && original != null) {
                    val updateRequest = UpdateWorkRequest(
                        title = original.title, description = original.description, file_name = finalFileName,
                        raw_index = original.raw_index, stitch_index = original.stitch_index, is_completed = original.is_completed
                    )
                    ApiClient.service.updateWork(workId, updateRequest)
                    _uiState.value = PatternEditUiState.SaveSuccess(finalFileName)
                } else {
                    _uiState.value = PatternEditUiState.SaveSuccess(finalFileName)
                }
            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Failed to save pattern", e)
                _uiState.value = PatternEditUiState.Error("保存に失敗しました。")
            }
        }
    }

    fun resetToSuccess() {
        (_uiState.value as? PatternEditUiState.Success)?.let { _uiState.value = it }
    }
    private fun pushToUndoStack(grid: List<List<String>>) {
        undoStack.add(grid)
        (_uiState.value as? PatternEditUiState.Success)?.let { _uiState.value = it.copy(canUndo = true) }
    }
}