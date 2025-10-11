package com.example.myapplication.model

// ★★★ ここを正しいimport文に修正 ★★★
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkRequest(
    val title: String,
    val description: String,
    val file_name: String
)
