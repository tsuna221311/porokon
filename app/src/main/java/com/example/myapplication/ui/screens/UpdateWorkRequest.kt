package com.example.myapplication.model

/**
 * 作品情報を更新 (PUT /v1/works/{id}) する際にAPIに送信するデータ。
 * API仕様書に基づき、更新する作品の新しい情報をすべて含みます。
 */
data class UpdateWorkRequest(
    val title: String,
    val description: String?,
    val file_name: String, // 更新された編み図のファイル名
    val raw_index: Int,
    val stitch_index: Int,
    val is_completed: Boolean
)

