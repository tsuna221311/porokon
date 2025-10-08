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
 */
sealed interface OcrUiState {
    object Standby : OcrUiState
    object Loading : OcrUiState
    data class Success(val fileUrl: String) : OcrUiState
    data class Error(val message: String) : OcrUiState
}

class OcrViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<OcrUiState>(OcrUiState.Standby)
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    /**
     * URIで指定された画像をAPIサーバーにアップロードします。
     */
    fun uploadImage(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uiState.value = OcrUiState.Loading
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: throw Exception("Failed to open input stream")
                val requestBody = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())

                val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "image.jpg"

                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestBody)

                val response = ApiClient.service.uploadImage(multipartBody)
                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {
                    _uiState.value = OcrUiState.Success(responseBody.csv_url)
                } else {
                    throw Exception("Upload failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("OcrViewModel", "Error uploading image", e)
                _uiState.value = OcrUiState.Error("画像のアップロードに失敗しました。")
            }
        }
    }

    /**
     * UIの状態を初期状態に戻します。
     */
    fun resetState() {
        _uiState.value = OcrUiState.Standby
    }
}
