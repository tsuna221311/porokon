package com.example.myapplication.model

import java.time.Instant

// ダッシュボードで表示する作品データの定義です
data class Work(
    val id: Int,
    val title: String,
    val work_url: String,
    val row_index: Int,
    val stitch_index: Int,
    val is_completed: Boolean,
    val description: String,
    val completed_at: Instant?, // A completed work might not have a completed_at time, so it can be nullable
    val updated_at: String,
    val created_at: String
)

