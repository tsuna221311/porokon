package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// UIの状態を表すデータクラス
data class PatternEditUiState(
    val patternGrid: List<List<String>> = emptyList(),
    val selectedSymbol: String = "k",
    val selectedCell: Pair<Int, Int>? = null,
    val canUndo: Boolean = false // 「元に戻す」が可能か
)

class PatternEditViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PatternEditUiState())
    val uiState: StateFlow<PatternEditUiState> = _uiState.asStateFlow()

    // 変更履歴を保存するためのリスト
    private val history = mutableListOf<List<List<String>>>()

    init {
        loadInitialPattern()
    }

    private fun loadInitialPattern() {
        // 本来はカウンター画面から渡された編み図データを読み込む
        val initialGrid = List(10) { List(10) { "-" } }
        _uiState.update { it.copy(patternGrid = initialGrid) }
        // 最初の状態を履歴に保存
        history.add(initialGrid)
    }

    // 記号パレットで記号が選択されたときの処理
    fun onSymbolSelected(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol) }
    }

    // グリッドのセルがクリックされたときの処理
    fun onCellClicked(row: Int, col: Int) {
        val currentGrid = _uiState.value.patternGrid
        // 新しいグリッド状態を作成
        val newGrid = currentGrid.map { it.toMutableList() }.toMutableList()
        newGrid[row][col] = _uiState.value.selectedSymbol

        // 変更をUIに反映し、履歴を更新
        updateGridAndHistory(newGrid)
        _uiState.update { it.copy(selectedCell = Pair(row, col)) }
    }

    // 行を追加するロジック
    fun addRow() {
        val currentGrid = _uiState.value.patternGrid
        val columnCount = currentGrid.firstOrNull()?.size ?: 10
        val newRow = List(columnCount) { "-" }
        val newGrid = currentGrid.toMutableList().apply { add(newRow) }

        updateGridAndHistory(newGrid)
    }

    // 行を削除するロジック
    fun removeRow() {
        val currentGrid = _uiState.value.patternGrid
        if (currentGrid.size > 1) { // 最後の1行は消せないようにする
            val newGrid = currentGrid.toMutableList().apply { removeLast() }
            updateGridAndHistory(newGrid)
        }
    }

    // 「元に戻す」ロジック
    fun undo() {
        if (history.size > 1) { // 最初の状態より前には戻れない
            history.removeLast() // 最新の履歴を削除
            val previousGrid = history.last()
            _uiState.update {
                it.copy(
                    patternGrid = previousGrid,
                    canUndo = history.size > 1 // 履歴が1つなら、もう元に戻せない
                )
            }
        }
    }

    // 保存するロジック
    fun savePattern() {
        // TODO: ここで、現在のグリッド(_uiState.value.patternGrid)を
        // CSV形式に変換し、データベースやファイルに保存する処理を実装する
        Log.d("PatternEdit", "Pattern Saved!")
    }

    // グリッドの状態と履歴を同時に更新するヘルパー関数
    private fun updateGridAndHistory(newGrid: List<List<String>>) {
        history.add(newGrid)
        _uiState.update {
            it.copy(
                patternGrid = newGrid,
                canUndo = history.size > 1 // 履歴が1つより多ければ元に戻せる
            )
        }
    }
}

