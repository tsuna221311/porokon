package com.example.myapplication.ui.components.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 修正: Routes の代わりに Screen をインポート
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.SecondarySalmon

@Composable
fun AppDrawer(onDestinationClicked: (String) -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "あみナビ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SecondarySalmon,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            // 修正: Routes.DASHBOARD -> Screen.Dashboard.route
            DrawerItem(
                label = "ダッシュボード",
                icon = Icons.Default.Dashboard,
                route = Screen.Dashboard.route,
                onClick = onDestinationClicked
            )
            // 修正: Routes.MY_PATTERNS -> Screen.MyPatterns.route
            DrawerItem(
                label = "マイ編み図",
                icon = Icons.Default.Book,
                route = Screen.MyPatterns.route,
                onClick = onDestinationClicked
            )
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, route: String, onClick: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(route) }
            .padding(vertical = 12.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Color.Gray)
        Spacer(Modifier.width(16.dp))
        Text(label)
    }
}
