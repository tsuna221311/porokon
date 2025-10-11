package com.example.myapplication.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.CreateWorkRequest
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UIの状態定義
data class SavePatternUiState(
    val title: String = "",
    val memo: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val error: String? = null
)

class SavePatternViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(SavePatternUiState())
    val uiState: StateFlow<SavePatternUiState> = _uiState.asStateFlow()

    // 前の画面から渡された、保存済みのCSVファイル名
    private val fileUrl: String = savedStateHandle.get<String>("fileUrl") ?: ""

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onMemoChange(newMemo: String) {
        _uiState.update { it.copy(memo = newMemo) }
    }

    fun saveWork() {
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(error = "タイトルを入力してください。") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val request = CreateWorkRequest(
                    title = _uiState.value.title,
                    description = _uiState.value.memo,
                    file_name = fileUrl
                )
                // APIを呼び出して作品情報をサーバーに保存
                ApiClient.service.createWork(request)
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "保存に失敗しました: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}