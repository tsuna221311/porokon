package com.example.myapplication.ui.screens

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * OCR（画像アップロード）フローのUIが取りうる状態を定義します。
 * 成功時のデータを一つのクラスにまとめ、isChartフラグで種類を判別します。
 */
sealed interface OcrUiState {
    object Standby : OcrUiState
    object Loading : OcrUiState
    data class Success(val initialContent: String, val isChart: Boolean) : OcrUiState
    data class Error(val message: String) : OcrUiState
}

class OcrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<OcrUiState>(OcrUiState.Standby)
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    fun uploadImage(uri: Uri, contentResolver: ContentResolver, isChart: Boolean) {
        viewModelScope.launch {
            _uiState.value = OcrUiState.Loading
            try {

                // --- 画像データの準備 (共通) ---
                val inputStream = contentResolver.openInputStream(uri) ?: throw Exception("Failed to open input stream")
                val requestBody = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())
                val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "image.jpg"
                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestBody)

                // isChartの値に応じてAPIを呼び分ける
                if (isChart) {
                    val response = ApiClient.service.uploadChartImage(multipartBody)
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        _uiState.value = OcrUiState.Success(body.csv, true)
                    } else { throw Exception("Chart conversion API failed") }
                } else {
                    val response = ApiClient.service.uploadOcrImage(multipartBody)
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        _uiState.value = OcrUiState.Success(body.pattern, false)
                    } else { throw Exception("OCR API failed") }
                }
            } catch (e: Exception) {
                Log.e("OcrViewModel", "Error uploading image", e)
                _uiState.value = OcrUiState.Error("画像の解析に失敗しました。")
            }
        }
    }

    fun resetState() {
        _uiState.value = OcrUiState.Standby
    }
}
