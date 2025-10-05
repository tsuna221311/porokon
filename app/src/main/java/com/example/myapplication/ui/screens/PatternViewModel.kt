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

// PatternView画面のUIの状態
data class PatternUiState(
    val work: Work? = null,
    val isLoading: Boolean = true
)

class PatternViewModel(
    savedStateHandle: SavedStateHandle // ナビゲーションから引数を受け取るために必要
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternUiState())
    val uiState: StateFlow<PatternUiState> = _uiState.asStateFlow()

    // NavControllerから渡されたIDを取得
    private val workId: Int = savedStateHandle.get<Int>("workId") ?: 0

    init {
        // 作品データを読み込む
        loadWork(workId)
    }

    private fun loadWork(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: ここで実際にデータベースから workId に対応する作品データを取得する
            // このサンプルでは、ダミーデータを作成する
            val dummyWork = Work(
                id = id,
                title = "サンプル編み図 (ID: $id)",
                description = "基本の2目ゴム編み",
                work_url = "",
                rowIndex = 5,                // 修正: row_index -> rowIndex
                stitchIndex = 0,             // 修正: stitch_index -> stitchIndex
                is_completed = false,
                completed_at = Instant.now().toString(), // 修正: .toString() を追加
                created_at = Instant.now().toString(),
                updated_at = Instant.now().toString()
            )
            _uiState.update { it.copy(work = dummyWork, isLoading = false) }
        }
    }

    // カウンターの段数を増やす
    fun incrementRow() {
        _uiState.update { currentState ->
            currentState.work?.let { work ->
                val newRowIndex = work.rowIndex + 1 // 修正: .row_index -> .rowIndex
                // TODO: データベースの rowIndex も更新する
                currentState.copy(work = work.copy(rowIndex = newRowIndex)) // 修正: row_index -> rowIndex
            } ?: currentState
        }
    }

    // カウンターの段数を減らす
    fun decrementRow() {
        _uiState.update { currentState ->
            currentState.work?.let { work ->
                val newRowIndex = (work.rowIndex - 1).coerceAtLeast(0) // 修正: .row_index -> .rowIndex
                // TODO: データベースの rowIndex も更新する
                currentState.copy(work = work.copy(rowIndex = newRowIndex)) // 修正: row_index -> rowIndex
            } ?: currentState
        }
    }
}