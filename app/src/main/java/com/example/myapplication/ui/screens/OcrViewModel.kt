package com.example.myapplication.ui.screens

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// UIの状態を表す
sealed interface OcrUiState {
    object Idle : OcrUiState
    data class ImageCaptured(val uri: Uri) : OcrUiState
    object Processing : OcrUiState
    data class TextExtracted(val text: String) : OcrUiState
    data class CsvGenerated(val csv: String, val patternName: String) : OcrUiState
    data class Error(val message: String) : OcrUiState
}

class OcrViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OcrUiState>(OcrUiState.Idle)
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun onImageCaptured(uri: Uri?) {
        if (uri != null) {
            _uiState.update { OcrUiState.ImageCaptured(uri) }
        } else {
            _uiState.update { OcrUiState.Error("写真の撮影に失敗しました。") }
        }
    }

    fun retakePhoto() {
        _uiState.update { OcrUiState.Idle }
    }

    fun processImageToText(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { OcrUiState.Processing }
            try {
                // --- 擬似OCR処理 ---
                kotlinx.coroutines.delay(2000)
                val dummyText = "Row 1: *K2, P2; rep from * to end.\nRow 2: *K2, P2; rep from * to end."
                _uiState.update { OcrUiState.TextExtracted(dummyText) }
            } catch (e: Exception) {
                _uiState.update { OcrUiState.Error("テキスト抽出に失敗しました。") }
            }
        }
    }

    fun onTextConfirmed(text: String) {
        val dummyCsv = "k,k,p,p,k,k,p,p\nk,k,p,p,k,k,p,p"
        _uiState.update { OcrUiState.CsvGenerated(dummyCsv, "新しい作品") }
    }

    /**
     * CSVをサーバーにアップロードする（仮のRetrofit呼び出し）
     */
    fun uploadCsv(csv: String, patternName: String) {
        viewModelScope.launch {
            _uiState.update { OcrUiState.Processing }
            try {
                // CSVをRequestBodyに変換
                val requestBody = csv.toRequestBody("text/csv".toMediaTypeOrNull())
                val multipart = MultipartBody.Part.createFormData("file", "$patternName.csv", requestBody)

                // 仮の Retrofit 呼び出し
                val response = ApiClient.service.uploadCsv(multipart) // ★ Retrofit側で uploadCsv を定義しておく
                if (response.isSuccessful) {
                    _uiState.update { OcrUiState.CsvGenerated(csv, patternName) }
                } else {
                    _uiState.update { OcrUiState.Error("CSVのアップロードに失敗しました。") }
                }
            } catch (e: Exception) {
                Log.e("OcrViewModel", "CSVアップロード失敗", e)
                _uiState.update { OcrUiState.Error("CSVのアップロードに失敗しました。") }
            }
        }
    }
}
