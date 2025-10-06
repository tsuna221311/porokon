package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

// Sample pattern data (ideally from ViewModel)
val patternData = List(8) {
    listOf("k", "k", "p", "p", "k", "k", "p", "p", "k", "k", "p", "p")
}
val symbolMap = mapOf("k" to "|", "p" to "•")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternViewScreen(
    navController: NavController,
    viewModel: PatternDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val work = uiState.work
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(work?.title ?: "Loading...", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EnglishPattern.route) }) {
                        Icon(Icons.Default.Translate, contentDescription = "Translate")
                    }
                    IconButton(onClick = { navController.navigate(Screen.PatternEdit.route) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        },
        containerColor = BgBase
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (work != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(selected = selectedTabIndex == 0, onClick = { selectedTabIndex = 0 }, text = { Text("Surface") })
                    Tab(selected = selectedTabIndex == 1, onClick = { selectedTabIndex = 1 }, text = { Text("Reverse") })
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PatternChart(
                        pattern = patternData, // TODO: This should come from the ViewModel
                        highlightedRow = work.raw_index, // Corrected to raw_index
                        modifier = Modifier.weight(1f)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Row", fontWeight = FontWeight.SemiBold)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CounterButton(text = "-") { viewModel.decrementRow() }
                                Text(
                                    text = (work.raw_index + 1).toString(), // Corrected to raw_index
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal
                                )
                                CounterButton(text = "+") { viewModel.incrementRow() }
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.error ?: "An error occurred.")
            }
        }
    }
}

@Composable
fun PatternChart(
    pattern: List<List<String>>,
    highlightedRow: Int,
    modifier: Modifier = Modifier
) {
    if (pattern.isEmpty()) return

    val columnCount = pattern.first().size
    val highlightColor = PrimaryTeal.copy(alpha = 0.2f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            userScrollEnabled = false
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
                        text = symbolMap[symbol] ?: "?",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CounterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
