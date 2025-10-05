package com.example.myapplication.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Work
<<<<<<<< HEAD:app/src/main/java/com/example/myapplication/ui/screens/PatternViewModel.kt
========
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.GCSApiClient
>>>>>>>> 285eced38b1821a087fad6355c780d7f14e637b4:app/src/main/java/com/example/myapplication/ui/screens/PatternDetailViewModel.kt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

<<<<<<<< HEAD:app/src/main/java/com/example/myapplication/ui/screens/PatternViewModel.kt
// PatternView画面のUIの状態
data class PatternUiState(
    val work: Work? = null,
    val isLoading: Boolean = true
)
========
class PatternDetailViewModel (
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = savedStateHandle.get<Int>("workId") ?: 100
>>>>>>>> 285eced38b1821a087fad6355c780d7f14e637b4:app/src/main/java/com/example/myapplication/ui/screens/PatternDetailViewModel.kt

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
<<<<<<<< HEAD:app/src/main/java/com/example/myapplication/ui/screens/PatternViewModel.kt
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
========
            try {
                val body = ApiClient.service.getOneWork(id).body()
                    _work.value = body
                    fetchCsv(body?.work_url ?: "")

            } catch (e: Exception) {
                Log.e("PatternViewModel", "エラー", e)
                _work.value = null
            }
>>>>>>>> 285eced38b1821a087fad6355c780d7f14e637b4:app/src/main/java/com/example/myapplication/ui/screens/PatternDetailViewModel.kt
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

<<<<<<<< HEAD:app/src/main/java/com/example/myapplication/ui/screens/PatternViewModel.kt
    // カウンターの段数を減らす
    fun decrementRow() {
        _uiState.update { currentState ->
            currentState.work?.let { work ->
                val newRowIndex = (work.rowIndex - 1).coerceAtLeast(0) // 修正: .row_index -> .rowIndex
                // TODO: データベースの rowIndex も更新する
                currentState.copy(work = work.copy(rowIndex = newRowIndex)) // 修正: row_index -> rowIndex
            } ?: currentState
========
    private fun fetchCsv(signedUrl: String) {
        viewModelScope.launch {
            try {
                val response = GCSApiClient.service.downloadCsv(signedUrl)
                if (response.isSuccessful) {
                    val csvText = response.body()?.string() ?: ""
                    Log.d("csvText",csvText) // CSV の内容
                } else {
                    Log.d("csvText","Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
>>>>>>>> 285eced38b1821a087fad6355c780d7f14e637b4:app/src/main/java/com/example/myapplication/ui/screens/PatternDetailViewModel.kt
        }
    }
}