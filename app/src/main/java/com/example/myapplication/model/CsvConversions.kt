package com.example.myapplication.model

/**
 * 画像アップロードAPI (`/v1/csv-conversions`) からのレスポンスを格納するデータクラス。
 * csv: 解析された編み図のCSVテキストデータ
 * csv_url: 保存されたCSVファイルのURL
 */
data class CsvConversions(
    val csv: String,
    val csv_url: String
)

