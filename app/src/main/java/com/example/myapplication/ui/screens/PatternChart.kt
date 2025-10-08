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
 */
@Composable
fun PatternChart(
    pattern: List<List<String>>,
    highlightedRow: Int,
    modifier: Modifier = Modifier
) {
    val symbolMap = mapOf("k" to "|", "p" to "•")
    val highlightColor = PrimaryTeal.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pattern.isEmpty()) {
            Text("Pattern data is not available.", color = Color.Gray)
        } else {
            val columnCount = pattern.firstOrNull()?.size ?: 1
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount),
                userScrollEnabled = true
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
                            .aspectRatio(1f)
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbolMap[symbol] ?: symbol,
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
