package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.PrimaryTeal

/**
 * 編み図のグリッドを表示するための、再利用可能なUIコンポーネント。
 * @param pattern 表示する編み図の2次元リストデータ。
 * @param highlightedRow ハイライト表示する行のインデックス。
 * @param modifier このコンポーザブルに適用するModifier。
 */
@Composable
fun PatternChart(
    pattern: List<List<String>>,
    highlightedRow: Int,
    modifier: Modifier = Modifier
) {
    // CSV表記と画面に表示する記号の対応表
    val symbolMap = mapOf(
        "k" to "|", "p" to "•",
        "k2tog" to "╱", "ssk" to "╲",
        "yo" to "○", "c4f" to "交", "c4b" to "差"
        // 他の記号も必要に応じてここに追加します
    )
    val highlightColor = PrimaryTeal.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pattern.isEmpty()) {
            Text("編み図データがありません", color = Color.Gray)
        } else {
            val columnCount = pattern.firstOrNull()?.size ?: 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                userScrollEnabled = true // 長い編み図はスクロールできるようにする
            ) {
                itemsIndexed(pattern.flatten()) { index, symbol ->
                    val rowIndex = index / columnCount
                    val backgroundColor = if (rowIndex == highlightedRow) {
                        highlightColor
                    } else {
                        Color.Transparent
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f) // セルを正方形に保つ
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        // '^' は複数マス記号の一部なので、何も表示しない
                        if (symbol != "^") {
                            Text(
                                text = symbolMap[symbol] ?: symbol, // 不明な記号はそのまま表示
                                color = Color.DarkGray,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}