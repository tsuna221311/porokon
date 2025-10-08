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

// 編み図編集画面のUIの状態を管理するデータクラス
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

    // 前の画面から渡された、編集対象の作品ID
    private val workId: Int = checkNotNull(savedStateHandle["workId"])
    // 「元に戻す」機能のための編集履歴スタック
    private val undoStack = mutableListOf<List<List<String>>>()

    init {
        // ViewModelが作成されたら、API経由で編み図データを読み込む
        loadPatternForEdit(workId)
    }

    private fun loadPatternForEdit(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. APIで作品情報を取得し、編み図(CSV)のURLを得る
                val workResponse = ApiClient.service.getOneWork(id)
                val workBody = workResponse.body()
                if (!workResponse.isSuccessful || workBody == null || workBody.work_url.isBlank()) {
                    throw Exception("Failed to get work info or work_url is empty")
                }

                // 2. GCSからCSVデータをダウンロードする
                val csvResponse = GCSApiClient.service.downloadCsv(workBody.work_url)
                val csvBody = csvResponse.body()
                if (!csvResponse.isSuccessful || csvBody == null) {
                    throw Exception("Failed to download CSV data")
                }

                // 3. 取得したCSVテキストを2次元リストに変換してUIの状態を更新
                val grid = csvBody.string().lines().mapNotNull { if (it.isNotBlank()) it.split(",") else null }
                _uiState.update { it.copy(patternGrid = grid, isLoading = false) }

            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Error loading pattern", e)
                _uiState.update { it.copy(error = "編み図の読み込みに失敗しました", isLoading = false) }
            }
        }
    }

    /**
     * UIで編み図のセルがクリックされたときに呼び出される
     */
    fun onCellClicked(row: Int, col: Int) {
        pushToUndoStack()
        val newGrid = _uiState.value.patternGrid.map { it.toMutableList() }.toMutableList()
        newGrid[row][col] = _uiState.value.selectedSymbol
        _uiState.update { it.copy(patternGrid = newGrid, selectedCell = Pair(row, col)) }
    }

    /**
     * UIで記号パレットの記号が選択されたときに呼び出される
     */
    fun onSymbolSelected(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol) }
    }

    fun addRow() {
        pushToUndoStack()
        val currentGrid = _uiState.value.patternGrid
        val newRow = List(currentGrid.firstOrNull()?.size ?: 10) { "-" } // 列数は既存の行に合わせる
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

    /**
     * 編集内容をサーバーに保存する。
     */
    fun savePattern() {
        viewModelScope.launch {
            try {
                // 1. 現在の編み図グリッドをCSV形式の文字列に変換
                val csvContent = _uiState.value.patternGrid.joinToString("\n") { it.joinToString(",") }

                // 2. CSV文字列をAPI送信用に変換
                val requestBody = csvContent.toRequestBody("text/csv".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "updated_pattern.csv", requestBody)

                // 3. APIを呼び出して、新しいCSVをアップロードし、そのURLを取得
                val uploadResponse = ApiClient.service.uploadCsv(multipartBody)
                val newUrl = uploadResponse.body()?.csv_url ?: throw Exception("CSV upload failed")

                // 4. 新しく取得したURLで、作品情報を更新するAPIを呼び出す
                val updateRequest = UpdateWorkRequest(work_url = newUrl)
                ApiClient.service.updateWork(workId, updateRequest)

                // TODO: 保存成功をUIに通知する（例: Toast, Snackbar）

            } catch (e: Exception) {
                Log.e("PatternEditViewModel", "Failed to save pattern", e)
                // TODO: 保存失敗をUIに通知する
            }
        }
    }

    private fun pushToUndoStack() {
        undoStack.add(_uiState.value.patternGrid)
        _uiState.update { it.copy(canUndo = true) }
    }
}
