package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.dummyEnglishPatternInstructions
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed interface EditEnglishPatternUiState {
    object InitialLoading : EditEnglishPatternUiState
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

    // private val initialContent: String? = savedStateHandle["csvContent"] // この行はもう不要

    init {
        // ★★★ ここから修正 ★★★
        // 渡されたデータを無視し、常にDummyData.ktの英文パターンを使用する

        // ダミーの英文パターン（List<String>）を、改行で区切られた1つの文字列に変換
        val dummyText = dummyEnglishPatternInstructions.joinToString("\n")

        // UIの状態を常にダミーデータで更新
        _uiState.value = EditEnglishPatternUiState.Success(dummyText)
        // ★★★ ここまで修正 ★★★
    }

    fun onTextChanged(newText: String) {
        if (_uiState.value is EditEnglishPatternUiState.Success) {
            _uiState.value = EditEnglishPatternUiState.Success(newText)
        }
    }

    fun convertEnglishToCsvString(): String {
        val currentState = _uiState.value
        val currentText = if (currentState is EditEnglishPatternUiState.Success) currentState.text else ""
        return currentText
            .lines()
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n")
    }

    fun saveEditedPattern(editedText: String) {
        viewModelScope.launch {
            _uiState.value = EditEnglishPatternUiState.Saving
            try {
                val requestBody = editedText.toRequestBody("text/plain".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("file", "edited_pattern.txt", requestBody)
                val response = ApiClient.service.uploadFixedCsv(multipartBody)
                val newFileName = response.body()?.file_name ?: throw Exception("Edited pattern upload failed")
                _uiState.value = EditEnglishPatternUiState.SaveSuccess(newFileName)
            } catch (e: Exception) {
                Log.e("EditEnglishPatternVM", "Failed to save edited pattern", e)
                _uiState.value = EditEnglishPatternUiState.Error("保存に失敗しました。")
            }
        }
    }

    fun resetState() {
        (_uiState.value as? EditEnglishPatternUiState.Success)?.text?.let {
            _uiState.value = EditEnglishPatternUiState.Success(it)
        }
    }
}

