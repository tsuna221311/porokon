package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// OCR画面のUIの状態を定義する
sealed interface OcrUiState {
    object Idle : OcrUiState // 初期状態
    data class ImageCaptured(val uri: Uri) : OcrUiState // 写真撮影後の確認状態
    object Processing : OcrUiState // 処理中
    data class TextExtracted(val text: String) : OcrUiState // テキスト抽出完了
    data class CsvGenerated(val csv: String, val patternName: String) : OcrUiState // CSV生成完了
    data class Error(val message: String) : OcrUiState // エラー発生
}

class OcrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OcrUiState>(OcrUiState.Idle)
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    // 写真が撮影されたときにUIから呼び出される
    fun onImageCaptured(uri: Uri?) {
        if (uri != null) {
            _uiState.update { OcrUiState.ImageCaptured(uri) }
        } else {
            _uiState.update { OcrUiState.Error("写真の撮影に失敗しました。") }
        }
    }

    // 「撮り直す」が押されたときに呼び出される
    fun retakePhoto() {
        _uiState.update { OcrUiState.Idle }
    }

    // 「この写真を使う」が押されたときに呼び出される (API通信のシミュレーション)
    fun processImageToText(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { OcrUiState.Processing }
            try {
                // --- 本来はここで外部APIを呼び出す ---
                delay(2000) // 2秒待って、APIからの応答をシミュレート
                val dummyText = "Row 1: *K2, P2; rep from * to end.\nRow 2: *K2, P2; rep from * to end."
                // ------------------------------------
                _uiState.update { OcrUiState.TextExtracted(dummyText) }
            } catch (e: Exception) {
                _uiState.update { OcrUiState.Error("テキストの抽出に失敗しました。") }
            }
        }
    }

    // ユーザーがテキストを修正した後に呼び出される
    fun onTextConfirmed(text: String) {
        // --- ここでテキストを解析してCSVに変換するロジックを呼び出す ---
        val dummyCsv = "k,k,p,p,k,k,p,p\nk,k,p,p,k,k,p,p"
        // -----------------------------------------------------------
        _uiState.update { OcrUiState.CsvGenerated(dummyCsv, "新しい作品") }
    }

    // CSVを作品として保存する
    fun saveWork(csv: String, patternName: String) {
        // --- ここでデータベースに新しいWorkを保存するロジックを実装する ---
        println("作品名: $patternName")
        println("CSVデータ:\n$csv")
        println("を保存しました！")
        // -----------------------------------------------------------------
    }
}

