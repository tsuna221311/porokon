package com.example.myapplication.model

/**
 * APIから受け取る作品データの完全な形式。
 * プロパティ名はAPIのJSONレスポンスと完全に一致させています。
 * 一部のプロパティはAPIの応答によってnullになる可能性があるため、Null許容型(?)にしています。
 */
data class Work(
    val id: Int,
    val title: String,
    val description: String?,
    val work_url: String?,      // 作品詳細でCSVをダウンロードするためのURL
    val file_name: String?,     // 新規作成時や編集時に使うファイル名
    val raw_index: Int,
    val stitch_index: Int,
    val is_completed: Boolean,
    val completed_at: String?,
    val updated_at: String,
    val created_at: String
)

