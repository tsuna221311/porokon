package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.dummyPatternFromImage // ★★★ 1. ダミーデータをインポート ★★★
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PatternUiState(
    val work: Work? = null,
    val patternData: List<List<String>> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// ★★★ 2. このファイル内のダミーデータ定義は削除 ★★★

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
                val response = ApiClient.service.getOneWork(id)
                if (response.isSuccessful) {
                    val work = response.body()
                    _uiState.update {
                        it.copy(
                            work = work,
                            patternData = dummyPatternFromImage, // ★★★ 3. インポートしたデータを参照 ★★★
                            isLoading = false
                        )
                    }
                } else {
                    throw Exception("API call failed with code ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PatternDetailViewModel", "Error loading work", e)
                _uiState.update { it.copy(error = "作品の読み込みに失敗しました。", isLoading = false) }
            }
        }
    }
    // ... (カウンターのロジックは変更なし)
    private fun updateStitchCount(work: Work, newRow: Int, newStitch: Int) {
        viewModelScope.launch {
            try {
                val request = IncrementStitchRequest(
                    raw_index = newRow,
                    stitch_index = newStitch,
                    is_completed = work.is_completed
                )
                val response = ApiClient.service.incrementStitch(work.id, request)
                if(response.isSuccessful) {
                    _uiState.update { it.copy(work = response.body()) }
                } else {
                    Log.e("PatternDetailViewModel", "Failed to update stitch count: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PatternDetailViewModel", "Error updating stitch count", e)
            }
        }
    }
    fun incrementRow() { _uiState.value.work?.let { updateStitchCount(it, it.raw_index + 1, it.stitch_index) } }
    fun decrementRow() { _uiState.value.work?.let { if (it.raw_index > 0) { updateStitchCount(it, it.raw_index - 1, it.stitch_index) } } }
    fun incrementStitch() { _uiState.value.work?.let { updateStitchCount(it, it.raw_index, it.stitch_index + 1) } }
    fun decrementStitch() { _uiState.value.work?.let { if (it.stitch_index > 0) { updateStitchCount(it, it.raw_index, it.stitch_index - 1) } } }
}

