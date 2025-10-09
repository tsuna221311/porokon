package com.example.myapplication.model

/**
 * 画像から英文へのOCR API (/v1/ocr) からのレスポンス。
 * API仕様書に基づき、認識されたテキストを保持します。
 */
data class OcrResponse(
    val pattern: String
)

