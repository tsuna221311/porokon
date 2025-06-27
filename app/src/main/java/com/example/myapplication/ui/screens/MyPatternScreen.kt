package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.PatternListItem
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.SecondarySalmon

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
