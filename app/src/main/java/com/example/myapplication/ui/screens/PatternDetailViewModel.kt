package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.GCSApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PatternUiState(
    val work: Work? = null,
    val patternData: List<List<String>> = emptyList(),
    val patternName: String? = null, // CSVから読み取った名前用
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
                val response = ApiClient.service.getOneWork(id)
                if (response.isSuccessful) {
                    val work = response.body()
                    _uiState.update { it.copy(work = work) }

                    // ★★★ 修正: work_url -> file_name ★★★
                    work?.file_name?.let { url ->
                        if(url.isNotBlank()) fetchCsv(url)
                    } ?: _uiState.update { it.copy(isLoading = false) } // URLがなければロード完了
                } else { throw HttpException(response) }
            } catch (e: Exception) {
                Log.e("PatternDetailViewModel", "Error loading work", e)
                _uiState.update { it.copy(error = "作品の読み込みに失敗しました。", isLoading = false) }
            }
        }
    }

    private fun fetchCsv(signedUrl: String) {
        viewModelScope.launch {
            try {
                val response = GCSApiClient.service.downloadCsv(signedUrl)
                if (response.isSuccessful) {
                    val csvText = response.body()?.string() ?: ""

                    val lines = csvText.lines()
                    val metadata = mutableMapOf<String, String>()
                    val gridData = mutableListOf<List<String>>()

                    lines.forEach { line ->
                        if (line.startsWith("#")) {
                            val parts = line.removePrefix("#").split(',', limit = 2)
                            if (parts.size == 2) {
                                metadata[parts[0].trim()] = parts[1].trim()
                            }
                        } else if (line.isNotBlank()) {
                            gridData.add(line.split(','))
                        }
                    }

                    _uiState.update {
                        it.copy(
                            patternData = gridData,
                            patternName = metadata["name"],
                            isLoading = false
                        )
                    }
                } else { throw HttpException(response) }
            } catch (e: Exception) {
                Log.e("PatternDetailViewModel", "Error fetching CSV", e)
                _uiState.update { it.copy(error = "編み図データの読み込みに失敗しました。", isLoading = false) }
            }
        }
    }

    /**
     * サーバーにカウンターの更新を通知する共通関数
     */
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

    // --- UIから呼び出されるカウンター操作関数 ---

    fun incrementRow() {
        _uiState.value.work?.let {
            updateStitchCount(it, it.raw_index + 1, it.stitch_index)
        }
    }

    fun decrementRow() {
        _uiState.value.work?.let {
            if (it.raw_index > 0) {
                updateStitchCount(it, it.raw_index - 1, it.stitch_index)
            }
        }
    }

    fun incrementStitch() {
        _uiState.value.work?.let {
            updateStitchCount(it, it.raw_index, it.stitch_index + 1)
        }
    }

    fun decrementStitch() {
        _uiState.value.work?.let {
            if (it.stitch_index > 0) {
                updateStitchCount(it, it.raw_index, it.stitch_index - 1)
            }
        }
    }
}

