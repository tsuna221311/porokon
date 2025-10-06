package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.counter.CompactCounterRow
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDetailScreen(
    navController: NavController,
    viewModel: PatternDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val work = uiState.work

    var selectedTab by remember { mutableStateOf(0) }
    var sensorConnected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = work?.title ?: "Loading...") },
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
        }
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
                    .background(BgBase)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Surface") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Reverse") })
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pattern chart will be displayed here", color = Color.Gray)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactCounterRow(
                        label = "Current Row",
                        count = work.raw_index,
                        onCountChange = { newCount ->
                            if (newCount > work.raw_index) viewModel.incrementRow() else viewModel.decrementRow()
                        }
                    )
                    Divider(color = BgBase, thickness = 1.dp)
                    CompactCounterRow(
                        label = "Current Stitch",
                        count = work.stitch_index,
                        onCountChange = { newCount ->
                            if (newCount > work.stitch_index) viewModel.incrementStitch() else viewModel.decrementStitch()
                        }
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) { Text("Row Mode") }
                        Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) { Text("Stitch Mode", color = Color.DarkGray) }
                    }
                    Divider(color = BgBase, thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sensor Connection", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                        Switch(
                            checked = sensorConnected,
                            onCheckedChange = { sensorConnected = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal)
                        )
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

