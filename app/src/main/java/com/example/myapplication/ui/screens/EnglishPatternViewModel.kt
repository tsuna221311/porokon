package com.example.myapplication.ui.screens

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

class EnglishPatternViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EnglishPatternUiState())
    val uiState: StateFlow<EnglishPatternUiState> = _uiState.asStateFlow()

    init {
        // ViewModelが作成されたときに、ダミーの編み図を翻訳する
        translateDummyPattern()
    }

    private fun translateDummyPattern() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // ダミーデータを使って翻訳処理を実行
            val result = translate(dummyPatternForTranslation)

            _uiState.update { it.copy(translatedPattern = result, isLoading = false) }
        }
    }

    /**
     * 編み図データ（2次元リスト）を英文パターンに変換するロジック。
     * @param pattern 編み図のグリッドデータ。
     * @return 翻訳されたパターン(手順と略語)。
     */
    private fun translate(pattern: List<List<String>>): TranslatedPattern {
        val instructions = mutableListOf<String>()
        val usedSymbols = mutableSetOf<String>()

        // 各行をループして処理
        pattern.forEach { row ->
            if (row.isEmpty()) return@forEach
            // '^' と '-' を除いた、実際に使われている記号を収集
            usedSymbols.addAll(row.filter { it != "^" && it != "-" })

            val compressedRow = StringBuilder()
            var count = 0
            var currentSymbol = ""

            row.forEach { symbol ->
                if (symbol == currentSymbol && (symbol == "k" || symbol == "p")) {
                    count++
                } else {
                    if (count > 0) {
                        compressedRow.append(formatSymbol(currentSymbol, count))
                        compressedRow.append(", ")
                    }
                    currentSymbol = symbol
                    count = 1
                }
            }
            if (count > 0) {
                compressedRow.append(formatSymbol(currentSymbol, count))
            }

            instructions.add(compressedRow.toString().trim().removeSuffix(","))
        }

        // 収集した記号から略語のマップを作成
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

    /**
     * 記号と回数から、"K2"や"k2tog"のような文字列を生成するヘルパー関数。
     */
    private fun formatSymbol(symbol: String, count: Int): String {
        return when {
            symbol == "-" -> "" // 空白マスは無視
            symbol == "^" -> "" // 複数マス記号の一部は無視
            count > 1 && (symbol == "k" || symbol == "p") -> "${symbol.uppercase()}$count"
            else -> symbol
        }
    }
}
