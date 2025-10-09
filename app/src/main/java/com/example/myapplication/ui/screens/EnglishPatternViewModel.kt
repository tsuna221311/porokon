package com.example.myapplication.ui.screens

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.logic.PatternTranslator
import com.example.myapplication.logic.TranslatedPattern
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * 英文パターン表示画面のUIが取りうる状態を定義します。
 */
sealed interface EnglishPatternUiState {
    object Loading : EnglishPatternUiState
    data class Success(
        val translatedPattern: TranslatedPattern,
        val originalCsv: String,
        val highlightedRow: Int = 0
    ) : EnglishPatternUiState
    data class Error(val message: String) : EnglishPatternUiState
}

// --- サーバー通信をシミュレートするためのモック（模擬）クラス ---
interface OcrApiService {
    suspend fun uploadImageForOcr(imageBytes: ByteArray): String
}

class MockOcrApiService : OcrApiService {
    override suspend fun uploadImageForOcr(imageBytes: ByteArray): String {
        delay(1500)
        return "k,k,p,p\nk,k,p,p\np,p,k,k\np,p,k,k"
    }
}
// --- ここまでモッククラス ---

class EnglishPatternViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<EnglishPatternUiState>(EnglishPatternUiState.Loading)
    val uiState: StateFlow<EnglishPatternUiState> = _uiState.asStateFlow()

    private val ocrService: OcrApiService = MockOcrApiService()

    init {
        // ★★★ 修正点: 存在しないファイル名(img_3315)を正しいファイル名(img_3135)に修正 ★★★
        loadPatternFromRemoteOcr(R.raw.img_3135)
    }

    fun loadPatternFromRemoteOcr(imageResourceId: Int) {
        viewModelScope.launch {
            _uiState.value = EnglishPatternUiState.Loading
            try {
                val imageBytes = getApplication<Application>().resources.openRawResource(imageResourceId).use {
                    it.readBytes()
                }
                val csvContent = ocrService.uploadImageForOcr(imageBytes)
                if (csvContent.isBlank()) {
                    throw IOException("サーバーから空のデータが返されました。")
                }

                Log.d("CSVResponse", csvContent)

                val grid = parseCsvToGrid(csvContent)
                val translatedPattern = PatternTranslator.fromGridToEnglish(grid)

                _uiState.value = EnglishPatternUiState.Success(
                    translatedPattern = translatedPattern,
                    originalCsv = csvContent,
                    highlightedRow = 0
                )

            } catch (e: Exception) {
                _uiState.value = EnglishPatternUiState.Error("パターン変換に失敗しました: ${e.message}")
            }
        }
    }

    private fun parseCsvToGrid(csv: String): List<List<String>> {
        return csv.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                line.split(',').map { it.trim() }
            }
    }
}