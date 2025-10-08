package com.example.myapplication.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 翻訳されたパターンを保持するためのデータクラス
data class TranslatedPattern(
    val instructions: List<String>,
    val abbreviations: Map<String, String>
)

// 英文パターン画面のUIの状態を管理するデータクラス
data class EnglishPatternUiState(
    val translatedPattern: TranslatedPattern? = null,
    val highlightedRow: Int = 0,
    val isLoading: Boolean = true
)

// PatternViewScreenで定義されているものと同じダミーデータ
private val dummyPatternForTranslation = listOf(
    listOf("p", "p", "-", "-", "k", "k", "k", "-"),
    listOf("p", "p", "-", "-", "k2tog", "^", "k", "-"),
    listOf("p", "p", "-", "-", "k", "k", "k", "k"),
    listOf("p", "p", "-", "ssk", "^", "k", "k", "k"),
    listOf("p", "p", "-", "k", "k", "k", "k", "k"),
    listOf("p", "p", "-", "k", "k2tog", "^", "k", "k"),
    listOf("p", "p", "ssk", "^", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k2tog", "^", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k"),
    listOf("p", "p", "p", "-", "k", "k", "k", "k")
).reversed()

class EnglishPatternViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnglishPatternUiState())
    val uiState: StateFlow<EnglishPatternUiState> = _uiState.asStateFlow()

    // 前の画面から渡された、ハイライトする行のインデックス
    private val highlightedRow: Int = checkNotNull(savedStateHandle["highlightedRow"])

    init {
        // ViewModelが作成されたときに、ダミーの編み図を翻訳する
        translateDummyPattern()
    }

    private fun translateDummyPattern() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ダミーデータを使って翻訳処理を実行
            val result = translate(dummyPatternForTranslation)

            _uiState.update {
                it.copy(
                    translatedPattern = result,
                    highlightedRow = highlightedRow,
                    isLoading = false
                )
            }
        }
    }

    /**
     * 編み図データ（2次元リスト）を英文パターンに変換するロジック。
     */
    private fun translate(pattern: List<List<String>>): TranslatedPattern {
        val instructions = mutableListOf<String>()
        val usedSymbols = mutableSetOf<String>()

        pattern.forEach { row ->
            if (row.isEmpty()) return@forEach
            usedSymbols.addAll(row.filter { it != "^" && it != "-" })
            instructions.add(compressRow(row))
        }

        val abbreviations = usedSymbols.associateWith {
            when(it.lowercase()) {
                "k" -> "knit"
                "p" -> "purl"
                "k2tog" -> "knit 2 stitches together"
                "ssk" -> "slip, slip, knit"
                else -> "unknown symbol"
            }
        }

        return TranslatedPattern(instructions = instructions, abbreviations = abbreviations)
    }

    // 1行を圧縮して英文にするヘルパー関数
    private fun compressRow(row: List<String>): String {
        if (row.isEmpty()) return ""
        val parts = mutableListOf<String>()
        var count = 0
        var currentSymbol = ""

        row.forEach { symbol ->
            if (symbol == currentSymbol && (symbol == "k" || symbol == "p")) {
                count++
            } else {
                if(count > 0 && currentSymbol != "-") parts.add(formatSymbol(currentSymbol, count))
                currentSymbol = symbol
                count = 1
            }
        }
        if(count > 0 && currentSymbol != "-") parts.add(formatSymbol(currentSymbol, count))
        return parts.joinToString(", ").trim().removeSuffix(",")
    }

    private fun formatSymbol(symbol: String, count: Int): String {
        return when {
            symbol == "^" -> ""
            count > 1 && (symbol == "k" || symbol == "p") -> "${symbol.uppercase()}$count"
            else -> symbol
        }
    }
}
