package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import com.example.myapplication.logic.PatternTranslator
import com.example.myapplication.logic.TranslatedPattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 英文パターン画面のUIの状態
data class EnglishPatternUiState(
    val translatedPattern: TranslatedPattern? = null,
    val isLoading: Boolean = true
)

class EnglishPatternViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EnglishPatternUiState())
    val uiState: StateFlow<EnglishPatternUiState> = _uiState.asStateFlow()

    init {
        // ViewModelが作成されたときに、サンプルの編み図を翻訳する
        translateSamplePattern()
    }

    private fun translateSamplePattern() {
        // 本来は前の画面から渡されたCSVデータをここで使う
        val sampleCsvGrid = listOf(
            listOf("k", "k", "p", "p", "k", "k", "p", "p"),
            listOf("k", "k", "p", "p", "k", "k", "p", "p"),
            listOf("p", "p", "k", "k", "p", "p", "k", "k"),
            listOf("p", "p", "k", "k", "p", "p", "k", "k"),
            listOf("k2tog", "^", "yo", "p", "k", "k2tog", "^", "yo"),
        )

        val result = PatternTranslator.translate(sampleCsvGrid)
        _uiState.update { it.copy(translatedPattern = result, isLoading = false) }
    }
}
