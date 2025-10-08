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
                // URIから画像データを読み込み、API送信用に変換
                val inputStream = contentResolver.openInputStream(uri) ?: throw Exception("Failed to open input stream from URI.")
                val requestBody = inputStream.readBytes().toRequestBody("image/jpeg".toMediaTypeOrNull())

                // ファイル名を取得
                val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "image.jpg"

                // サーバーが受け取れる形式(Multipart)の部品を作成
                val multipartBody = MultipartBody.Part.createFormData("file", fileName, requestBody)

                // APIクライアントを呼び出して、実際に画像をアップロード
                val response = ApiClient.service.uploadImage(multipartBody)
                val responseBody = response.body()

                // APIからのレスポンスを安全に処理
                if (response.isSuccessful && responseBody != null) {
                    // csv_urlがnullでないことを確認してからSuccess状態にする
                    val csvUrl = responseBody.csv_url
                    if (!csvUrl.isNullOrBlank()) {
                        _uiState.value = OcrUiState.Success(csvUrl)
                    } else {
                        // 通信は成功したが、期待したURLが含まれていなかった場合
                        throw Exception("API response is successful but csv_url is null or empty.")
                    }
                } else {
                    // APIがエラーを返した場合
                    throw Exception("Upload failed: ${response.message()}")
                }
            } catch (e: Exception) {
                // 通信失敗など、何か問題が起きたらUIを「エラー」状態にする
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
