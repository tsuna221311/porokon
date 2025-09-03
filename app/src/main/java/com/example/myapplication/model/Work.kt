package com.example.myapplication.model

import java.time.Instant

data class Work(
    val id: Int,
    val title: String,
    val work_url: String,
    val row_index: Int,
    val stitch_index: Int,
    val is_completed: Boolean,
    val description: String,
    val completed_at: Instant,
    val updated_at: String,
    val created_at: String
)