package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
 * 英文パターン修正画面のUIが取りうる状態を定義します。
 */
sealed interface EditEnglishPatternUiState {
    object InitialLoading : EditEnglishPatternUiState // ★★★ UIが期待しているこの状態を追加 ★★★
    data class Success(val text: String) : EditEnglishPatternUiState
    object Saving : EditEnglishPatternUiState
    data class SaveSuccess(val newFileUrl: String) : EditEnglishPatternUiState
    data class Error(val message: String) : EditEnglishPatternUiState
}

class EditEnglishPatternViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditEnglishPatternUiState>(EditEnglishPatternUiState.InitialLoading)
    val uiState: StateFlow<EditEnglishPatternUiState> = _uiState.asStateFlow()

    // 前の画面から渡されたOCR結果のテキスト
    private val initialContent: String? = savedStateHandle["csvUrl"]

    init {
        // ViewModelが作成されたら、受け取ったテキストでUIの状態を更新する
        if (initialContent != null) {
            _uiState.value = EditEnglishPatternUiState.Success(initialContent)
        } else {
            _uiState.value = EditEnglishPatternUiState.Error("編集するテキストがありません。")
        }
    }

    /**
     * テキストフィールドの入力が変更されるたびにUIから呼び出されます。
     */
    fun onTextChanged(newText: String) {
        if (_uiState.value is EditEnglishPatternUiState.Success) {
            _uiState.value = EditEnglishPatternUiState.Success(newText)
        }
    }

    /**
     * 「この内容で確定する」ボタンが押されたときにUIから呼び出されます。
     */
    fun saveEditedPattern(editedText: String) {
        viewModelScope.launch {
            _uiState.value = EditEnglishPatternUiState.Saving
            try {
                val requestBody = editedText.toRequestBody("text/csv".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "edited_pattern.csv", requestBody)

                val response = ApiClient.service.uploadFixedCsv(multipartBody)
                val newFileName = response.body()?.file_name ?: throw Exception("Edited CSV upload failed")

                _uiState.value = EditEnglishPatternUiState.SaveSuccess(newFileName)
            } catch (e: Exception) {
                Log.e("EditEnglishPatternVM", "Failed to save edited pattern", e)
                _uiState.value = EditEnglishPatternUiState.Error("保存に失敗しました。")
            }
        }
    }

    /**
     * エラー発生後などに、UIの状態を編集中に戻します。
     */
    fun resetState() {
        val currentText = (_uiState.value as? EditEnglishPatternUiState.Success)?.text
        if (currentText != null) {
            _uiState.value = EditEnglishPatternUiState.Success(currentText)
        }
    }
}