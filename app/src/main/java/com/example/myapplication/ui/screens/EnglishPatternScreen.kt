package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishPatternScreen(navController: NavController) {
    val currentStep = 5 // This should be passed from a ViewModel in a real app
    val (abbreviations, instructions) = remember {
        translateToEnglishPattern()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("英文パターン") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Chart")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                TextButton(onClick = { /* Copy to clipboard */ }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("パターンをコピー")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("文字サイズ", modifier = Modifier.padding(end = 16.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Abbreviations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    abbreviations.forEach { (abbr, desc) ->
                        Row {
                            Text("$abbr: ", fontWeight = FontWeight.Bold)
                            Text(desc)
                        }
                    }
                }
            }
            item {
                Text("Instructions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    instructions.forEachIndexed { index, instruction ->
                        val isHighlighted = (index + 1) == currentStep
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isHighlighted) PrimaryTeal.copy(alpha = 0.1f) else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isHighlighted) PrimaryTeal else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text("${index + 1}: ", fontWeight = FontWeight.Bold)
                            Text(instruction)
                        }
                    }
                }
            }
        }
    }
}

// Helper function for translation
private fun translateToEnglishPattern(): Pair<Map<String, String>, List<String>> {
    val abbreviations = mapOf(
        "K" to "knit",
        "P" to "purl",
        "rep" to "repeat"
    )

    // Simplified logic for 2x2 ribbing based on CSV
    val instructions = List(10) {
        "*K2, P2; rep from * to end."
    }

    return Pair(abbreviations, instructions)
}

