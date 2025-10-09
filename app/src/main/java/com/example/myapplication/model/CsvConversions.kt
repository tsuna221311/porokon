package com.example.myapplication.model

/**
 * 画像アップロードAPI (`/v1/csv-conversions` や `/v1/fix-csv`) からのレスポンスを格納するデータクラス。
 * プロパティ名はAPIのJSONレスポンスと一致させる必要があります。
 */
data class CsvConversions(
    val csv: String,
    val file_name: String // ★★★ 修正: API仕様書に合わせて csv_url -> file_name
)

