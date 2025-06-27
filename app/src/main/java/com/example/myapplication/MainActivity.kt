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

// --- Main App Structure ---
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigation(navController, drawerState, scope)
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
