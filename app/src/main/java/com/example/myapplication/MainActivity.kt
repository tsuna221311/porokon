package com.example.myapplication

import android.Manifest // ★カメラの権限に必要
import android.content.pm.PackageManager // ★カメラの権限に必要
import android.os.Bundle
import android.widget.Toast // ★メッセージ表示に必要
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult // ★カメラ起動に必要
import androidx.activity.result.contract.ActivityResultContracts // ★カメラ起動に必要
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat // ★カメラの権限に必要
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmuNaviTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

// --- Navigation ---
object Routes {
    const val DASHBOARD = "dashboard"
    const val MY_PATTERNS = "my_patterns"
    const val PATTERN_VIEW = "pattern_view"
    const val PATTERN_DETAIL = "pattern_detail"
}

// --- Main App Structure ---
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigation(navController, drawerState, scope)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPatternsScreen(onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("マイ編み図") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "メニュー")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                PatternListItem(title = "保存した編み図A", description = "靴下", icon = Icons.Default.Bookmark, iconColor = PrimaryTeal) {}
            }
            item {
                PatternListItem(title = "保存した編み図B", description = "帽子", icon = Icons.Default.Bookmark, iconColor = SecondarySalmon) {}
            }
        }
    }
}


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDetailScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("シンプルなマフラー") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("編み図詳細画面のコンテンツ")
        }
    }
}


@Composable
fun AppDrawer(onDestinationClicked: (String) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "あみナビ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SecondarySalmon, modifier = Modifier.padding(bottom = 16.dp))
            DrawerItem(label = "ダッシュボード", icon = Icons.Default.Dashboard, route = Routes.DASHBOARD, onClick = onDestinationClicked)
            DrawerItem(label = "マイ編み図", icon = Icons.Default.Book, route = Routes.MY_PATTERNS, onClick = onDestinationClicked)
            DrawerItem(label = "編み図ビュー", icon = Icons.Default.GridView, route = Routes.PATTERN_VIEW, onClick = onDestinationClicked)
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, route: String, onClick: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onClick(route) }.padding(vertical = 12.dp)) {
        Icon(icon, contentDescription = label, tint = Color.Gray); Spacer(Modifier.width(16.dp)); Text(label)
    }
}

@Composable
fun PatternListItem(title: String, description: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(iconColor.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = iconColor)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold); Text(text = description, fontSize = 14.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AmuNaviTheme {
        MainApp()
    }
}
