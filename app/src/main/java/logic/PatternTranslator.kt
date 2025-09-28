package com.example.myapplication.logic

// 翻訳結果を格納するためのデータクラス
data class TranslatedPattern(
    val abbreviations: Map<String, String>,
    val instructions: List<String>
)

object PatternTranslator {

    // CSV記号と英語名の対応表
    private val symbolDictionary = mapOf(
        "k" to "knit",
        "p" to "purl",
        "yo" to "yarn over",
        "k2tog" to "knit 2 stitches together",
        "ssk" to "slip, slip, knit",
        "c4f" to "cable 4 front",
        // ... 他の記号もここに追加 ...
    )

    /**
     * CSV形式の編み図データを英文パターンに変換するメイン関数
     * @param patternGrid CSVデータの二次元リスト
     * @return 翻訳されたパターン (TranslatedPattern)
     */
    fun translate(patternGrid: List<List<String>>): TranslatedPattern {
        val usedSymbols = patternGrid.flatten().toSet()
        val abbreviations = generateAbbreviations(usedSymbols)
        val instructions = patternGrid.mapIndexed { index, row ->
            val rowNumber = index + 1
            val isPurlSide = rowNumber % 2 == 0 // 偶数段は裏側と仮定
            val processedRow = if (isPurlSide) processPurlRow(row) else processKnitRow(row)
            "**Row ${rowNumber}:** ${processedRow}"
        }
        return TranslatedPattern(abbreviations, instructions)
    }

    // 使用されている記号から略語リストを生成する
    private fun generateAbbreviations(symbols: Set<String>): Map<String, String> {
        return symbols
            .filter { symbolDictionary.containsKey(it) }
            .associate { it.uppercase() to symbolDictionary.getValue(it) }
    }

    // 表側の段を処理する
    private fun processKnitRow(row: List<String>): String {
        return groupStitches(row)
    }

    // 裏側の段を処理する（表目と裏目を反転させる）
    private fun processPurlRow(row: List<String>): String {
        val invertedRow = row.map {
            when (it) {
                "k" -> "p"
                "p" -> "k"
                else -> it // 他の記号はそのまま
            }
        }
        return groupStitches(invertedRow)
    }

    // 連続する同じ記号をグループ化する (例: k, k, p, p -> K2, P2)
    private fun groupStitches(row: List<String>): String {
        if (row.isEmpty()) return ""

        val grouped = mutableListOf<String>()
        var i = 0
        while (i < row.size) {
            val currentSymbol = row[i]
            if (currentSymbol == "-" || currentSymbol == "^") {
                i++
                continue
            }

            var count = 1
            var j = i + 1
            // 複数マス記号の処理 (例: k2tog, ^)
            if (symbolDictionary.containsKey(currentSymbol) && currentSymbol.length > 1) {
                val consumedStitches = getConsumedStitches(currentSymbol)
                i += consumedStitches
                grouped.add(currentSymbol.uppercase())
                continue
            }
            // 単純な連続記号の処理
            while (j < row.size && row[j] == currentSymbol) {
                count++
                j++
            }
            val symbolText = currentSymbol.uppercase()
            grouped.add(if (count > 1) "$symbolText$count" else symbolText)
            i += count
        }
        // TODO: さらに高度な繰り返しパターンの検出（例: *K2, P2; rep from *）を追加できる
        return grouped.joinToString(", ")
    }

    // 記号が何マス消費するかを返す（簡易版）
    private fun getConsumedStitches(symbol: String): Int {
        return when {
            symbol.contains("2") -> 2
            symbol.contains("3") -> 3
            symbol.contains("4") -> 4
            else -> 1
        }
    }
}
