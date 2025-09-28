package com.example.myapplication.ui.screens

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// UIの状態を表すデータクラス
data class PatternEditUiState(
    val patternGrid: List<List<String>> = emptyList(),
    val selectedSymbol: String = "k",
    val selectedCell: Pair<Int, Int>? = null
)

class PatternEditViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PatternEditUiState())
    val uiState: StateFlow<PatternEditUiState> = _uiState.asStateFlow()

    init {
        loadInitialPattern()
    }

    // 最初は10x10の空のグリッドを読み込む
    private fun loadInitialPattern() {
        val initialGrid = List(10) { List(10) { "-" } }
        _uiState.update { it.copy(patternGrid = initialGrid) }
    }

    // 記号パレットで記号が選択されたときの処理
    fun onSymbolSelected(symbol: String) {
        _uiState.update { it.copy(selectedSymbol = symbol) }
    }

    // グリッドのセルがクリックされたときの処理
    fun onCellClicked(row: Int, col: Int) {
        val newGrid = _uiState.value.patternGrid.map { it.toMutableList() }.toMutableStateList()
        newGrid[row][col] = _uiState.value.selectedSymbol
        _uiState.update { it.copy(patternGrid = newGrid, selectedCell = Pair(row, col)) }
    }

    // TODO: 行を追加するロジック
    fun addRow() { }

    // TODO: 保存するロジック
    fun savePattern() { }
}