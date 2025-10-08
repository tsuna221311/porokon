// EnglishPatternViewModel.kt
package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.myapplication.logic.TranslatedPattern

// -------------------------------
// UI状態を表すデータクラス
// -------------------------------
data class EnglishPatternUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val translatedPattern: TranslatedPattern? = null,
    val currentStep: Int = 0,
    val showCopiedMessage: Boolean = false
)

// -------------------------------
// ViewModel
// -------------------------------
class EnglishPatternViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EnglishPatternUiState())
    val uiState: StateFlow<EnglishPatternUiState> = _uiState.asStateFlow()

    init {
        loadPattern()
    }

    // 仮のパターンデータ読み込み
    private fun loadPattern() {
        _uiState.value = EnglishPatternUiState(
            translatedPattern = TranslatedPattern(
                abbreviations = mapOf(
                    "k" to "knit",
                    "p" to "purl",
                    "yo" to "yarn over"
                ),
                instructions = listOf(
                    "Cast on 30 stitches.",
                    "Row 1: *K2, P2* repeat to end.",
                    "Row 2: *P2, K2* repeat to end.",
                    "Repeat rows 1 and 2 until desired length."
                )
            )
        )
    }

    // 「コピーしました！」メッセージを表示
    fun showCopiedMessage() {
        _uiState.value = _uiState.value.copy(showCopiedMessage = true)
    }

    // ステップを更新（例：次の行に進む）
    fun updateStep(newStep: Int) {
        _uiState.value = _uiState.value.copy(currentStep = newStep)
    }

    // エラーを設定
    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    // ローディング状態を設定
    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
}
