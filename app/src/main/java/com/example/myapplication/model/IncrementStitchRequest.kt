package com.example.myapplication.model;

data class IncrementStitchRequest(
    val raw_index: Int,
    val stitch_index: Int,
    val is_completed: Boolean
)