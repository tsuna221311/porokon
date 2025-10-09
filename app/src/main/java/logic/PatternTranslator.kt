package com.example.myapplication.logic

/**
 * 翻訳された編み図のデータを保持します。
 * @property instructions 編み方の手順を文字列のリストで表現します。(例: ["K2, P2", "P2, K2"])
 * @property abbreviations 編み図で使われている略語とその正式名称のマップです。(例: {"k" to "knit", "p" to "purl"})
 */
data class TranslatedPattern(
    val instructions: List<String>,
    val abbreviations: Map<String, String>
)

/**
 * 編み図のグリッドデータと英文パターン形式を相互に変換するシングルトンオブジェクト。
 * このファイルがプロジェクトにおける唯一の定義となるようにしてください。
 */
object PatternTranslator {

    // 編み物記号、英文名、日本語名、記号が占める目数を定義
    private val symbolDetails = mapOf(
        "k" to Triple("knit", "表目", 1),
        "p" to Triple("purl", "裏目", 1),
        "yo" to Triple("yarn over", "かけ目", 1),
        "kfb" to Triple("knit front and back", "ねじり増し目", 1),
        "m1l" to Triple("make 1 left", "左増し目", 1),
        "m1r" to Triple("make 1 right", "右増し目", 1),
        "k2tog" to Triple("knit 2 together", "右上2目一度", 2),
        "p2tog" to Triple("purl 2 together", "（裏目の）右上2目一度", 2),
        "ssk" to Triple("slip, slip, knit", "左上2目一度", 2),
        "skpo" to Triple("slip, knit, pass over", "左上2目一度", 2),
        "k3tog" to Triple("knit 3 together", "右上3目一度", 3),
        "s2kp" to Triple("slip 2, knit, pass over", "中上3目一度", 3),
        "c4f" to Triple("cable 4 front", "左上交差 (2x2)", 4),
        "c4b" to Triple("cable 4 back", "右上交差 (2x2)", 4),
        "c6f" to Triple("cable 6 front", "左上交差 (3x3)", 6),
        "c6b" to Triple("cable 6 back", "右上交差 (3x3)", 6),
        "sl" to Triple("slip", "すべり目", 1),
        "bob" to Triple("bobble", "玉編み", 1)
    )

    /**
     * グリッドデータから英文パターンを生成します。
     * @param grid 記号の2次元リストで表現された編み図データ。
     * @return 英文パターンの手順と略語リストを含む [TranslatedPattern] オブジェクト。
     */
    fun fromGridToEnglish(grid: List<List<String>>): TranslatedPattern {
        val instructions = mutableListOf<String>()
        val usedSymbols = mutableSetOf<String>()

        grid.forEach { row ->
            if (row.isEmpty()) return@forEach
            // '^' (前の記号に続く) と '-' (空白) 以外の記号を収集
            usedSymbols.addAll(row.filter { it != "^" && it != "-" })
            instructions.add(compressRow(row))
        }

        // 使用された記号に対応する略語リストを作成
        val abbreviations = usedSymbols.associateWith { symbol ->
            symbolDetails[symbol.lowercase()]?.first ?: "unknown symbol"
        }

        return TranslatedPattern(instructions = instructions, abbreviations = abbreviations)
    }

    /**
     * 英文パターンからグリッドデータを生成します。
     * @param instructions 英文パターンの手順が書かれた文字列のリスト。
     * @return 記号の2次元リストで表現された編み図データ。
     */
    fun fromEnglishToGrid(instructions: List<String>): List<List<String>> {
        val grid = mutableListOf<List<String>>()
        // "k2" や "c4f" のようなパターンを抽出するための正規表現
        val regex = "([a-zA-Z]+[0-9]?[a-zA-Z]*)(\\d*)".toRegex()

        instructions.forEach { instruction ->
            val row = mutableListOf<String>()
            val parts = instruction.split(',').map { it.trim() }

            parts.forEach { part ->
                val match = regex.find(part)
                if (match != null) {
                    val symbol = match.groupValues[1].lowercase()
                    val count = match.groupValues[2].toIntOrNull() ?: 1

                    val stitchWidth = symbolDetails[symbol]?.third ?: 1

                    // 複数目を一度に処理する記号の場合 (例: c4f)
                    if (stitchWidth > 1) {
                        row.add(symbol)
                        repeat(stitchWidth - 1) { row.add("^") } // 後続のマスを '^' で埋める
                    } else {
                        // 1目ずつの記号の場合 (例: k, p)
                        repeat(count) { row.add(symbol) }
                    }
                }
            }
            grid.add(row)
        }
        return grid
    }

    /**
     * グリッドの1行を圧縮して、"K2, P2"のような文字列に変換します。
     */
    private fun compressRow(row: List<String>): String {
        if (row.isEmpty()) return ""
        val parts = mutableListOf<String>()
        var i = 0
        while (i < row.size) {
            val symbol = row[i]
            if (symbol == "-") { // 空白は無視
                i++
                continue
            }
            val stitchWidth = symbolDetails[symbol]?.third ?: 1
            if (stitchWidth > 1) { // 交差編みなど、複数マスを使う記号
                parts.add(symbol)
                i += stitchWidth
            } else { // 1目ずつの編み方
                var count = 0
                // 同じ記号がいくつ続くか数える
                while (i + count < row.size && row[i + count] == symbol) {
                    count++
                }
                parts.add(formatSymbol(symbol, count))
                i += count
            }
        }
        return parts.joinToString(", ")
    }

    /**
     * 記号と回数から、"K2" や "P" のような文字列を生成します。
     */
    private fun formatSymbol(symbol: String, count: Int): String {
        // 回数が1回の場合は数字をつけず、記号を大文字にする (例: "K")
        // 複数回の場合は記号を大文字にして回数を付与する (例: "P2")
        return if (count > 1) "${symbol.uppercase()}$count" else symbol.uppercase()
    }
}
