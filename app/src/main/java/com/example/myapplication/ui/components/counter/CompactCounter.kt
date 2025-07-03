package com.example.myapplication.ui.components.counter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.BorderDefault
import com.example.myapplication.ui.theme.PrimaryTeal

@Composable
fun CompactCounterRow(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CompactCounterButton(text = "-") { if (value > 0) onValueChange(value - 1) }
            Text("$value", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
            CompactCounterButton(text = "+") { onValueChange(value + 1) }
        }
    }
}

@Composable
fun CompactCounterButton(text: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(32.dp).border(1.dp, BorderDefault, CircleShape)) {
        Text(text, fontSize = 20.sp, color = Color.DarkGray, fontWeight = FontWeight.Light)
    }
}