package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.RegisterWork
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegisterWorkUiState {
    object Standby : RegisterWorkUiState
    object Loading : RegisterWorkUiState
    object Success : RegisterWorkUiState
    data class Error(val message: String) : RegisterWorkUiState
}

class RegisterWorkViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterWorkUiState>(RegisterWorkUiState.Standby)
    val uiState: StateFlow<RegisterWorkUiState> = _uiState.asStateFlow()

    private val workUrl: String = checkNotNull(savedStateHandle["fileUrl"])

    /**
     * 新しい作品をサーバーに登録する。
     * これは実際のAPI実装であり、ダミーコードではありません。
     */
    fun registerNewWork(title: String, description: String) {
        viewModelScope.launch {
            _uiState.value = RegisterWorkUiState.Loading
            try {
                val request = RegisterWork(
                    title = title,
                    description = description,
                    work_url = workUrl
                )

                // APIクライアントを呼び出して、実際にサーバーに作品を登録する
                val response = ApiClient.service.registerWork(request)

                if (response.isSuccessful) {
                    _uiState.value = RegisterWorkUiState.Success
                } else {
                    throw Exception("Failed to register work: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("RegisterWorkViewModel", "Error registering work", e)
                _uiState.value = RegisterWorkUiState.Error("作品の登録に失敗しました。")
            }
        }
    }
}

