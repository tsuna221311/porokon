package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.counter.CompactCounterRow
import com.example.myapplication.ui.theme.BgBase
import com.example.myapplication.ui.theme.PrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternViewScreen(onMenuClick: () -> Unit) {
    var rowCount by remember { mutableStateOf(12) }
    var stitchCount by remember { mutableStateOf(16) }
    var selectedTab by remember { mutableStateOf(0) }
    var sensorConnected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("サンプル編み図") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 通知処理 */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "通知")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BgBase)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("表面") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("裏面") })
            }
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("ここに編み図チャートが表示されます", color = Color.Gray)
            }
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactCounterRow("現在の段数", rowCount) { newCount -> rowCount = newCount }
                Divider(color = BgBase, thickness = 1.dp)
                CompactCounterRow("現在の目数", stitchCount) { newCount -> stitchCount = newCount }
            }
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)) { Text("段数モード") }
                    Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) { Text("目数モード", color = Color.DarkGray) }
                }
                Divider(color = BgBase, thickness = 1.dp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("センサー接続", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Switch(
                        checked = sensorConnected,
                        onCheckedChange = { sensorConnected = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryTeal)
                    )
                }
            }
        }
    }
}